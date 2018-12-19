package com.klmklm.meeting_attendance.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "meeting")
@EntityListeners(Meeting.class)
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer meetingId;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;

    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
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

    @Override
    public String toString() {
        return String.format("title: %s\nstart time: %s\nend time: %s", title, startTime, endTime);
    }
}