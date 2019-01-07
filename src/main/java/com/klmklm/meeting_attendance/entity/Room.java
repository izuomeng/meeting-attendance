package com.klmklm.meeting_attendance.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "room")
public class Room {
    @Id
    private Integer id;
    private String roomName;
    private String location;
    @OneToMany
    private List<Equipment> equipment;

    public String getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }
}
