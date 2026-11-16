package com.suntravels.api;

import com.suntravels.contract.ContractNotFoundException;
import com.suntravels.contract.InvalidContractException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ContractNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({InvalidContractException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception ex) {
        String message = ex instanceof InvalidContractException
                ? ex.getMessage() : "Request contains invalid or missing fields";
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
}
