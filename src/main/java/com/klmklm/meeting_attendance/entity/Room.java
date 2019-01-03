package com.klmklm.meeting_attendance.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "room")
public class Room {
    @Id
    private Integer id;
    private String roomName;
    private String location;

    public String getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }
}
