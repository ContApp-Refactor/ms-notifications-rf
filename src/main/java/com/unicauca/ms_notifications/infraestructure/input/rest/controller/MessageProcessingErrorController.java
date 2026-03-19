package com.unicauca.ms_notifications.infraestructure.input.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unicauca.ms_notifications.application.input.IMessageProcessingErrorCommandPort;
import com.unicauca.ms_notifications.application.input.IMessageProcessingErrorQueryPort;
import com.unicauca.ms_notifications.infraestructure.input.rest.mapper.IMessageProcessingErrorRestMapper;
import com.unicauca.ms_notifications.domain.model.MessageProcessingError;
import com.unicauca.ms_notifications.infraestructure.input.rest.dto.response.ApiResponse;
import com.unicauca.ms_notifications.infraestructure.input.rest.dto.response.MessageProcessingErrorResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/message-processing-errors")
public class MessageProcessingErrorController {
    private final IMessageProcessingErrorQueryPort queryUseCase;
    private final IMessageProcessingErrorCommandPort commandUseCase;
    private final IMessageProcessingErrorRestMapper restMapper;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageProcessingErrorResponse>> findById(Long id) {
        MessageProcessingError error = queryUseCase.findById(id);

        if (error == null) {
            return ResponseEntity.ok(
                    ApiResponse.successEmpty("MessageProcessingError with ID " + id + " not found.",
                            "NO_CONTENT"));
        }

        MessageProcessingErrorResponse responseDto = restMapper.toResponse(error);
        return ResponseEntity.ok(
                ApiResponse.success(responseDto, "MessageProcessingError with ID " + id + " found successfully."));
    }

    /**
     * @brief Retrieves the most recent message processing error
     * @return Response with the latest message processing error or not found
     */
    @GetMapping("/last")
    public ResponseEntity<ApiResponse<MessageProcessingErrorResponse>> findLastRecord() {
        MessageProcessingError error = queryUseCase.findLastRecord();

        if (error == null) {
            return ResponseEntity.ok(
                    ApiResponse.successEmpty("No message processing errors found.",
                            "NO_CONTENT"));
        }

        MessageProcessingErrorResponse responseDto = restMapper.toResponse(error);
        return ResponseEntity.ok(
                ApiResponse.success(responseDto, "Latest message processing error found successfully."));
    }

    /**
     * @brief Deletes all message processing error records
     * @return Response confirming deletion
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<Void>> deleteAll() {
        commandUseCase.deleteAll();
        return ResponseEntity.ok(ApiResponse.success(null, "All message processing errors deleted successfully."));
    }
}

