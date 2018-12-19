package com.klmklm.meeting_attendance.lib;

public class Response {
    /*错误码*/
    private Integer code;

    /*提示信息 */
    private String message;

    /*具体内容*/
    private Object data;

    private Response(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Response success(Object object) {
        return new Response(200, "", object);
    }

    public static Response success() {
        return new Response(200, "", null);
    }

    public static Response error(Integer code, String message) {
        return new Response(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
