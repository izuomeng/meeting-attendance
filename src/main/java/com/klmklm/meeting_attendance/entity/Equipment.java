package com.klmklm.meeting_attendance.entity;

import com.klmklm.meeting_attendance.lib.JsonConverter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table
public class Equipment {
    @Id
    @GeneratedValue
    private Integer id;
    private String type;
    private Integer inUse; // 0：未启用 1：启用
    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> info;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public Integer getInUse() {
        return inUse;
    }

    public void setInUse(Integer inUse) {
        this.inUse = inUse;
    }
}
