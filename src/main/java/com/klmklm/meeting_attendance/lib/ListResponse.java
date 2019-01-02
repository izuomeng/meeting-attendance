package com.klmklm.meeting_attendance.lib;

import java.util.List;

public class ListResponse<T> {
    private List<T> list;

    public ListResponse(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public ListResponse<T> success() {
        return this;
    }
}
