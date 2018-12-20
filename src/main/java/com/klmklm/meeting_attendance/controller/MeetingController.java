package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.Meeting;
import com.klmklm.meeting_attendance.lib.Response;
import com.klmklm.meeting_attendance.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MeetingController {

    private MeetingService meetingService;

    @Autowired
    MeetingController(MeetingService m) {
        this.meetingService = m;
    }

    private List<Meeting> getCustomMeeting(Predicate<Meeting> func) {
        List<Meeting> meetings = meetingService.findAll();

        return meetings
                .stream()
                .filter(func)
                .collect(Collectors.toList());
    }

    // 获取所有会议
    @GetMapping("/all-meetings")
    public Response getAllMeetings() {
        return Response.success(meetingService.findAll());
    }

    // 正在进行的会议列表
    @GetMapping("/ongoing-meetings")
    public Response getMeetings() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return Response.success(getCustomMeeting(
                meeting -> now.before(meeting.getEndTime()) && now.after(meeting.getStartTime())
        ));
    }

    // 已完成会议
    @GetMapping("/finished-meetings")
    public Response getFinishedMeetings() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return Response.success(
                getCustomMeeting(meeting -> now.after(meeting.getEndTime()))
        );
    }

    // 未开始会议
    @GetMapping("/waiting-meetings")
    public Response getWaitingMeetings() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return Response.success(
                getCustomMeeting(meeting -> now.before(meeting.getStartTime()))
        );
    }

    // 获取某个会议的详细信息
    @GetMapping("/meeting/{id}")
    public Response getMeetingDetail(@PathVariable Integer id) {
        return Response.success(meetingService.findById(id));
    }
}