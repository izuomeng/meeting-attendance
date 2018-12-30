package com.klmklm.meeting_attendance.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableResponse extends Response {
    private TableResponse(Integer status,String message,Object data) {
        super(status,message,data);
    }

    public static TableResponse success(List<Object> list) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return new TableResponse(0, "", result);
    }
}
