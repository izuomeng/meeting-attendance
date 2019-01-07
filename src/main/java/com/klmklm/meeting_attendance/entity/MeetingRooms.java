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
    private Integer signWay; // 0:统一签到; 1:入场签到
    private Integer collectHz; // 单位秒
    private Integer collectOutInfo; // 0:采集; 1:不采集

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

    public Integer getSignWay() {
        return signWay;
    }

    public void setSignWay(Integer signWay) {
        this.signWay = signWay;
    }

    public Integer getCollectHz() {
        return collectHz;
    }

    public void setCollectHz(Integer collectHz) {
        this.collectHz = collectHz;
    }

    public Integer getCollectOutInfo() {
        return collectOutInfo;
    }

    public void setCollectOutInfo(Integer collectOutInfo) {
        this.collectOutInfo = collectOutInfo;
    }
}
