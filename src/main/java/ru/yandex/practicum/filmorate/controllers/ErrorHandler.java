package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, DirectorNotFoundException.class,
            GenreNotFoundException.class, MpaNotFoundException.class, ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(RuntimeException e) {
        return  new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectUserData(ConstraintViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler ({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExceptions(ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка введенных данных");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptions(Throwable e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }



}
