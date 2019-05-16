package com.klmklm.meeting_attendance.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "meeting_user")
public class MeetingUser {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer attendance; // 0 for no and 1 for yes
    private Integer state; // 0: 未响应; 1: 已拒绝; 2:确认参加
    private Timestamp signTime;
    private String image;
    private String camera;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public void setState(Integer state) {
        this.state = state;
    }

    public Timestamp getSignTime() {
        return signTime;
    }

    public void setSignTime(Timestamp signTime) {
        this.signTime = signTime;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public User getUser() {
        return user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Integer getState() {
        return state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }
}
