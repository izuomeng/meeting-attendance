package com.klmklm.meeting_attendance.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp signTime;
    private String type;
    private String state;
    private String createBy;
    private Timestamp createTime;
    private Integer signWay; // 0:统一签到; 1:入场签到
    private Integer collectHz; // 单位秒
    private Integer collectOutInfo; // 0:采集; 1:不采集

    @ManyToMany
    private List<Room> rooms;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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