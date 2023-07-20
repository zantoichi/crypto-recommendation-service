package com.xm.cryptorecommendationservice.configuration.web;

import com.xm.cryptorecommendationservice.application.service.NoStatisticsFoundForCryptoException;
import com.xm.cryptorecommendationservice.infrastructure.web.CryptoNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoStatisticsFoundForCryptoException.class, CryptoNotSupportedException.class})
    public ProblemDetail handleRecordNotFoundException(Exception ex) {

        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeExceptions(RuntimeException ex) {

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }
}
