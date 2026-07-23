# 📧 MS Notifications Microservice (Sistema de Notificaciones)

## 📋 Descripción General

El microservicio **MS Notifications** es el sistema centralizado de **notificaciones por correo electrónico** del ecosistema CONTAPP. Consume eventos asíncrónos de otros microservicios (debt-payments, thirds-management, etc.) y envía notificaciones HTML enriquecidas con información de terceros. Mantiene réplicas de datos para garantizar consistencia y velocidad en el envío de emails.

### Puerto de Ejecución
```
http://localhost:8087
```

---

## 🎯 Responsabilidades Principales

1. **Envío de Emails** - Enviar notificaciones HTML mediante SMTP
2. **Consumo de Eventos** - Escuchar eventos de RabbitMQ
3. **Réplicas de Terceros** - Mantener datos sincronizados de clientes/proveedores
4. **Recordatorios de Facturas** - Notificar facturas próximas a vencer
5. **Notificación de Terceros** - Informar cambios en datos de terceros
6. **Auditoría de Errores** - Registrar errores en procesamiento de mensajes
7. **Reintentos** - Controlar reintentos automáticos en fallos

---

## 🏗️ Arquitectura

```
┌────────────────────────────────────┐
│  RabbitMQ Message Listeners        │
│  ├─ NotificationEventListener      │
│  └─ ThirdEventListener             │
├────────────────────────────────────┤
│  Services                          │
│  ├─ EmailAdapter                   │
│  ├─ ThirdReplicaService            │
│  └─ ErrorHandlingService           │
├────────────────────────────────────┤
│  REST Controllers (Auditoría)      │
│  └─ MessageProcessingErrorController
├────────────────────────────────────┤
│  Base de Datos (PostgreSQL)        │
│  ├─ third_replicas (caché)         │
│  └─ message_errors (auditoría)     │
└────────────────────────────────────┘
```

---

## 🔄 Event Listeners y Payloads

### 1. Third Event Listener - THIRD_UPDATED

**Exchange:** `third.updated.exchange`  
**Queue:** `third.updated.queue`  
**Routing Key:** `third.updated`

Escucha actualizaciones de terceros desde thirds-management y sincroniza réplica local.

#### Payload de Publicación (Prueba):
```json
{
  "properties": {
    "content_type": "application/json",
    "headers": {
      "x-jwt-token": "{{tokenKeycloak}}"
    }
  },
  "routing_key": "",
  "payload": "{\"type\":\"THIRD_UPDATED\",\"data\":{\"thirdId\":1,\"entId\":\"{{enterpriseId}}\",\"fullName\":\"Naren Alejandro Imbachi Quinayas\",\"email\":\"narenquinayas@gmail.com\",\"state\":true}}",
  "payload_encoding": "string"
}
```

#### Estructura del Evento (Decodificado):
```json
{
  "type": "THIRD_UPDATED",
  "data": {
    "thirdId": 1,
    "entId": "uuid-empresa-123",
    "fullName": "Naren Alejandro Imbachi Quinayas",
    "email": "narenquinayas@gmail.com",
    "state": true
  }
}
```

#### Procesamiento en Microservice:
```java
@RabbitListener(queues = "third.updated.queue")
public void handleThirdUpdated(String message) {
    // 1. Deserializar JSON
    ThirdUpdatedEvent event = objectMapper.readValue(message, ThirdUpdatedEvent.class);
    
    // 2. Actualizar réplica local
    ThirdReplicaEntity replica = new ThirdReplicaEntity();
    replica.setThirdId(event.getData().getThirdId());
    replica.setEntId(UUID.fromString(event.getData().getEntId()));
    replica.setFullName(event.getData().getFullName());
    replica.setEmail(event.getData().getEmail());
    replica.setState(event.getData().isState());
    thirdReplicaRepository.save(replica);
    
    // 3. Registrar en auditoría
    logEvent("THIRD_UPDATED", "SUCCESS");
}
```

#### Almacenamiento - ThirdReplicaEntity:
```sql
INSERT INTO third_replicas 
(third_id, enterprise_id, full_name, email, state, updated_at)
VALUES 
(1, 'uuid-123', 'Naren Alejandro Imbachi Quinayas', 'narenquinayas@gmail.com', true, NOW())
```

---

### 2. Notification Event Listener - INVOICE_DUE_REMINDER

**Exchange:** `notifications.exchange`  
**Queue:** `notifications.queue`  
**Routing Key:** `invoice.reminder`

Escucha recordatorios de facturas próximas a vencer desde debt-payments y envía emails.

#### Payload de Publicación (Prueba):
```json
{
  "properties": {
    "content_type": "application/json",
    "headers": {
      "x-jwt-token": "{{tokenKeycloak}}"
    }
  },
  "routing_key": "",
  "payload": "{\"type\":\"INVOICE_DUE_REMINDER\",\"data\":{\"thirdPartyId\":2,\"invoiceDetails\":[{\"invoiceId\":1001,\"invoiceCode\":9876,\"expirationDate\":\"2026-01-07\",\"totalAmount\":50000,\"pendingValue\":25000}]}}",
  "payload_encoding": "string"
}
```

#### Estructura del Evento (Decodificado):
```json
{
  "type": "INVOICE_DUE_REMINDER",
  "data": {
    "thirdPartyId": 2,
    "invoiceDetails": [
      {
        "invoiceId": 1001,
        "invoiceCode": 9876,
        "expirationDate": "2026-01-07",
        "totalAmount": 50000,
        "pendingValue": 25000
      }
    ]
  }
}
```

#### Procesamiento en Microservice:
```java
@RabbitListener(queues = "notifications.queue")
public void handleInvoiceDueReminder(String message) {
    try {
        // 1. Deserializar evento
        InvoiceDueReminderEvent event = objectMapper.readValue(message, InvoiceDueReminderEvent.class);
        
        // 2. Obtener datos del tercero desde réplica
        ThirdReplicaEntity third = thirdReplicaRepository.findByThirdId(event.getData().getThirdPartyId());
        if (third == null || third.getEmail() == null) {
            logError("INVOICE_DUE_REMINDER", "Third not found or email missing");
            return;
        }
        
        // 3. Construir contenido HTML del email
        String emailBody = buildInvoiceDueEmailHtml(third, event.getData().getInvoiceDetails());
        
        // 4. Enviar email
        emailAdapter.sendEmail(
            to: third.getEmail(),
            subject: "Recordatorio: Facturas Próximas a Vencer",
            body: emailBody,
            isHtml: true
        );
        
        // 5. Registrar éxito
        logEvent("INVOICE_DUE_REMINDER", "SUCCESS");
        
    } catch (Exception e) {
        // Registrar error para reintentos
        logError("INVOICE_DUE_REMINDER", e.getMessage());
    }
}
```

#### Email HTML Generado:
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recordatorio de Facturas Próximas a Vencer</title>
</head>
<body style="font-family: Arial, sans-serif;">
    <div style="max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px;">
        <h2>Recordatorio: Facturas Próximas a Vencer</h2>
        
        <p>Estimado/a <strong>Naren Alejandro Imbachi Quinayas</strong>,</p>
        
        <p>Le escribimos para recordarle que tiene las siguientes facturas próximas a vencer:</p>
        
        <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
            <thead>
                <tr style="background-color: #f0f0f0;">
                    <th style="border: 1px solid #ddd; padding: 10px;">Factura</th>
                    <th style="border: 1px solid #ddd; padding: 10px;">Fecha Vencimiento</th>
                    <th style="border: 1px solid #ddd; padding: 10px;">Total</th>
                    <th style="border: 1px solid #ddd; padding: 10px;">Pendiente</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td style="border: 1px solid #ddd; padding: 10px;">9876</td>
                    <td style="border: 1px solid #ddd; padding: 10px;">2026-01-07</td>
                    <td style="border: 1px solid #ddd; padding: 10px;">$50,000</td>
                    <td style="border: 1px solid #ddd; padding: 10px;">$25,000</td>
                </tr>
            </tbody>
        </table>
        
        <p>Le solicitamos hacer el pago de las cantidades pendientes lo antes posible.</p>
        
        <p>Cordialmente,<br/>
        <strong>Equipo de Contabilidad</strong><br/>
        CONTAPP
        </p>
    </div>
</body>
</html>
```

---

## 📡 Controladores (Auditoría)

### Message Processing Error Controller

REST API para consultar errores en la historia de procesamiento de eventos.

```
GET /api/notifications/errors                    - Listar errores
GET /api/notifications/errors/{id}               - Obtener detalle
GET /api/notifications/errors/status/{status}    - Filtrar por estado
POST /api/notifications/errors/{id}/retry        - Reintentar evento
DELETE /api/notifications/errors/{id}            - Eliminar error
```

#### Listar Errores (`GET /api/notifications/errors?status=FAILED&limit=20`)

```json
Response (200):
[
  {
    "id": 1,
    "messageType": "INVOICE_DUE_REMINDER",
    "status": "FAILED",
    "errorMessage": "Email address not found for third party ID 2",
    "errorStackTrace": "javax.mail.SendFailedException: ...",
    "retryCount": 2,
    "originatedAt": "2024-01-15T10:30:00Z",
    "lastRetryAt": "2024-01-15T10:45:00Z"
  }
]
```

#### Reintentar Evento (`POST /api/notifications/errors/1/retry`)

```json
Response (200):
{
  "id": 1,
  "status": "SUCCESS",
  "message": "Evento reintentado exitosamente",
  "retryCount": 3
}
```

---

## 📊 Entidades Principales

### ThirdReplicaEntity (Caché Local)
```java
@Entity
@Table(name = "third_replicas")
public class ThirdReplicaEntity {
    private Long id;
    private Long thirdId;                    // Referencia a tercero original
    private UUID enterpriseId;               // Empresa
    private String fullName;                 // Nombre completo
    private String email;                    // Email para contacto
    private boolean state;                   // Activo/Inactivo
    private LocalDateTime updatedAt;
}
```

### MessageProcessingErrorEntity (Auditoría)
```java
@Entity
@Table(name = "message_processing_errors")
public class MessageProcessingErrorEntity {
    private Long id;
    private String messageType;              // THIRD_UPDATED, INVOICE_DUE_REMINDER, etc.
    private String status;                   // PENDING, PROCESSING, SUCCESS, FAILED
    private String errorMessage;
    private String errorStackTrace;
    private Integer retryCount;              // Número de reintentos
    private LocalDateTime originatedAt;
    private LocalDateTime lastRetryAt;
}
```

---

## 📧 Email Adapter - Configuración SMTP

### Clase EmailAdapter
```java
@Component
public class EmailAdapter implements IEmailProviderPort {
    
    private final JavaMailSender javaMailSender;
    
    public void sendEmail(String to, String subject, String htmlBody, boolean isHtml) 
        throws MessagingException {
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@contapp.local");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Use HTML version");  // Fallback
        
        // Para HTML, usar MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        
        helper.setText(htmlBody, true);  // true para HTML
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("noreply@contapp.local");
        
        javaMailSender.send(mimeMessage);
    }
}
```

### Configuración en application.yml
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

---

## 🔐 Autenticación

Headers esperados en eventos RabbitMQ:
```
x-jwt-token: {{tokenKeycloak}}
```

El token JWT contiene:
- `sub` - ID del usuario
- `preferred_username` - Usuario
- `realm_access.roles` - Roles
- `resource_access` - Permisos por cliente

---

## 🗄️ Base de Datos

**Sistema:** PostgreSQL 15+

### Tablas Principales:
```sql
CREATE TABLE third_replicas (
    id BIGSERIAL PRIMARY KEY,
    third_id BIGINT NOT NULL,
    enterprise_id UUID NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(100),
    state BOOLEAN DEFAULT true,
    updated_at TIMESTAMP,
    UNIQUE(third_id, enterprise_id)
);

CREATE TABLE message_processing_errors (
    id BIGSERIAL PRIMARY KEY,
    message_type VARCHAR(100),
    status VARCHAR(20),
    error_message TEXT,
    error_stack_trace TEXT,
    retry_count INT DEFAULT 0,
    originated_at TIMESTAMP,
    last_retry_at TIMESTAMP
);

CREATE INDEX idx_third_replicas_email ON third_replicas(email);
CREATE INDEX idx_message_errors_status ON message_processing_errors(status);
CREATE INDEX idx_message_errors_type ON message_processing_errors(message_type);
```

---

## 🌐 Integración con Otros Microservicios

```
┌──────────────────────────────────┐
│  MS Notifications                │
├──────────────────────────────────┤
│                                  │
│  ↖← debt-payments:               │
│    - Publica INVOICE_DUE_REMINDER│
│                                  │
│  ↖← thirds-management:           │
│    - Publica THIRD_UPDATED       │
│    - Proporciona datos terceros  │
│                                  │
│  ↗← Eureka:                      │
│    - Se registra automáticamente │
└──────────────────────────────────┘
```

---

## 🚀 Configuración y Ejecución

### Requisitos
- Java 17+
- Spring Boot 3.4.8
- PostgreSQL 15+
- RabbitMQ 3.12+
- Servidor SMTP (Gmail, AWS SES, etc.)

### application.yml Completa
```yaml
spring:
  application:
    name: ms-notifications
  jpa:
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:postgresql://localhost:5432/contapp_notifications
    username: postgres
    password: password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

server:
  port: 8087

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

logging:
  level:
    com.ms_notifications: DEBUG
    org.springframework.amqp: DEBUG
```

### Ejecutar
```bash
mvn clean package
java -jar target/ms_notifications-0.0.1-SNAPSHOT.jar
```

---

## 📝 Validaciones y Reglas de Negocio

1. **Email Válido** - Validar formato de email antes de enviar
2. **Tercero Existe** - Verificar que replica local tenga email del tercero
3. **Reintentos Automáticos** - Máximo 3 reintentos en fallos de envío
4. **Auditoría Completa** - Registrar todos los eventos (éxito y fallo)
5. **Multi-tenancy** - Filtrar por `enterprise_id`
6. **Sincronización** - La réplica de terceros debe estar actualizada

---

## 🔧 Troubleshooting

### Error: "Email address not found"
- Verificar que el tercero tenga email en thirds-management
- Confirmar que ThirdReplicaEntity se actualizó con `THIRD_UPDATED`

### Error: "SMTP connection refused"
- Verificar que SMTP esté configurado correctamente
- Para Gmail: usar contraseña de aplicación, no contraseña de cuenta

### Error: "RabbitMQ connection refused"
- Asegurar RabbitMQ corriendo en puerto 5672
- Revisar credenciales en `application.yml`

### Error: "Message redelivered multiple times"
- El evento falló en 3 reintentos
- Consultar auditoría: `GET /api/notifications/errors/status/FAILED`
- Reintentar manualmente: `POST /api/notifications/errors/{id}/retry`

---

## 📊 Flujo de Envío de Email

```
RabbitMQ Event (INVOICE_DUE_REMINDER)
           ↓
Deserializar JSON
           ↓
Obtener email desde ThirdReplicaEntity
           ↓
Construir HTML del email
           ↓
JavaMailSender.send()
           ↓
¿Éxito? ──→ Registrar SUCCESS en auditoría
           ↓
¿Error? ──→ Registrar FAILED + ErrorStack + Incrementar retryCount
           ↓
¿Reintentos < 3? ──→ Programar reintento en 5 minutos
           ↓
¿Reintentos >= 3? ──→ Marcar como PERMANENT_FAILURE
```

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo:** Equipo Dinámica - Escuadrón Lobo  
**Repositorio:** https://github.com/Equipo-dinamita-escuadron-lobo/ms-notifications
**Issues:** [GitHub Issues](https://github.com/Equipo-dinamita-escuadron-lobo/ms-debt-payments/issues)

---

**Versión del documento:** 1.0
