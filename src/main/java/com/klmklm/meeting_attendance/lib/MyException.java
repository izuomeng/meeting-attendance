package com.klmklm.meeting_attendance.lib;

public class MyException extends Exception {
    private Integer code;

    public MyException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
