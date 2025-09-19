package com.taylor.photogalleryapp.exception;

import com.taylor.photogalleryapp.controller.web.PhotoWebController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice(assignableTypes = {PhotoWebController.class})
public class PageExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String handleIOExceptionPage(IOException ex, Model model) {
        model.addAttribute("errorMessage", "Could not load image: " + ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericPage(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Something went wrong: " + ex.getMessage());
        return "error";
    }
}

