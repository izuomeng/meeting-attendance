package com.klmklm.meeting_attendance.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "meeting_rooms")
@IdClass(MeetingRoomMultiKeys.class)
public class MeetingRooms {
    @Id
    @Column(name = "meeting_id")
    private Integer mid;
    @Id
    @Column(name = "rooms_id")
    private Integer rid;
    private Timestamp signTime;

    public MeetingRooms(){}

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Timestamp getSignTime() {
        return signTime;
    }

    public void setSignTime(Timestamp signTime) {
        this.signTime = signTime;
    }
}
