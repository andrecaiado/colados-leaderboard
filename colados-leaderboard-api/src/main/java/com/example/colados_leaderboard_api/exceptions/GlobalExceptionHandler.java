package com.example.colados_leaderboard_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomDataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleCustomDataIntegrityViolationException(CustomDataIntegrityViolationException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Data integrity violation occurred"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Invalid argument provided"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ErrorMessage> handleGameNotFound(EntityNotFound ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Entity not found"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MessageProcessingException.class)
    public ResponseEntity<ErrorMessage> handleMessageProcessingException(MessageProcessingException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Error processing message"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IncompleteGameResultsException.class)
    public ResponseEntity<ErrorMessage> handleIncompleteGameResultsException(IncompleteGameResultsException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Incomplete game results"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalGameStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalGameStateException(IllegalGameStateException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Illegal game state"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataInGameResultsException.class)
    public ResponseEntity<ErrorMessage> handleNonUniqueUserPlayerInGameResultsException(InvalidDataInGameResultsException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Invalid data in game results"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
