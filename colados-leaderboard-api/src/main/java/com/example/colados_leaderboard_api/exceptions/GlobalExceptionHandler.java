package com.example.colados_leaderboard_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChampionshipNameAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleChampionshipNameAlreadyExists(ChampionshipNameAlreadyExists ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new java.util.Date(),
                ex.getMessage(),
                "Championship name already exists"
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }
}
