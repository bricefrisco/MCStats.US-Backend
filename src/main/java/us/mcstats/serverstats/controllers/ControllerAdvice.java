package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import us.mcstats.serverstats.models.exceptions.ErrorResponse;

import java.sql.Timestamp;

@RestControllerAdvice
public class ControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onRuntimeException(RuntimeException e) {
        LOGGER.error("Error occurred", e);

        return new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage()
        );
    }
}
