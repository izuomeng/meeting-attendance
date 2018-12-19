package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.Meeting;
import com.klmklm.meeting_attendance.lib.Response;
import com.klmklm.meeting_attendance.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MeetingController {

    private MeetingService meetingService;

    @Autowired
    MeetingController(MeetingService m) {
        this.meetingService = m;
    }

    @GetMapping("/all-meetings")
    public List<Meeting> getAllMeetings() {
        return meetingService.findAll();
    }

    // 正在进行的会议列表
    @GetMapping("/ongoing-meetings")
    public List<Meeting> getMeetings(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<Meeting> meetings = meetingService.findAll();
        List<Meeting> result = new ArrayList<>();

        for (Meeting meeting: meetings) {
            Timestamp start = meeting.getStartTime();
            Timestamp end = meeting.getEndTime();
            if (now.before(end) && now.after(start)) {
                result.add(meeting);
            }
        }

        return result;
    }

    // 获取某个会议的详细信息
    @GetMapping("/meeting/{id}")
    public Response getMeetingDetail(@PathVariable Integer id) {
        return Response.success(meetingService.findById(id));
    }
}