package org.example.web.controllers;

import org.example.app.exceptions.BookShelfLoginNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class ErrorController {

    @GetMapping("/404")
    public String notFoundError() {
        return "errors/404";
    }

    @ExceptionHandler(BookShelfLoginNotFoundException.class)
    public String handleError(Model model, BookShelfLoginNotFoundException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/404";
    }
}
