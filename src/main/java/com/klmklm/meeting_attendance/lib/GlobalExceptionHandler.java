package com.klmklm.meeting_attendance.lib;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public Response jsonErrorHandler(MyException e) {
        return new Response(e.getCode(), e.getMessage(),null);
    }

}
