package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.Meeting;
import com.klmklm.meeting_attendance.entity.MeetingUser;
import com.klmklm.meeting_attendance.entity.Room;
import com.klmklm.meeting_attendance.entity.User;
import com.klmklm.meeting_attendance.lib.Response;
import com.klmklm.meeting_attendance.lib.TableResponse;
import com.klmklm.meeting_attendance.service.MeetingService;
import com.klmklm.meeting_attendance.service.MeetingUserService;
import com.klmklm.meeting_attendance.service.RoomService;
import com.klmklm.meeting_attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private MeetingUserService meetingUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;

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

    @GetMapping("/meeting")
    public Response getMeetingByTitle(@RequestParam("title") String title) {
        return Response.success(meetingService.findByTitle(title));
    }

    // 查看会议报名情况
    @GetMapping("/meeting/{id}/sign-info")
    public TableResponse getMeetingSignInfo(
            @PathVariable Integer id,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer type
    ) {
        return TableResponse.success(
                this
                        .meetingUserService
                        .findAllSignedUser(id)
                        .stream()
                        .filter(item -> {
                            switch (type) {
                                // 全部
                                case 1:
                                    return true;
                                // 已到场
                                case 2:
                                    return item.getAttendance() == 1;
                                // 未到场
                                case 3:
                                    return item.getAttendance() == 0;
                                default:
                                    return true;
                            }
                        })
                        .map(item -> {
                            Map<String, Object> result = new HashMap<>();
                            User user = item.getUser();
                            result.put("id", item.getId());
                            result.put("userName", user.getName());
                            result.put("email", user.getEmail());
                            result.put("phone", user.getPhone());
                            result.put("state", item.getState());
                            result.put("meetingRoom", item.getRoom().getRoomName());
                            result.put("signTime", item.getSignTime());
                            return result;
                        })
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/meeting-user")
    @Transactional
    public Response addMeetingUser(Integer uid, Integer mid, Integer rid) {
        MeetingUser meetingUser = new MeetingUser();
        Meeting meeting = meetingService.findById(mid).orElse(null);
        User user = userService.findById(uid).orElse(null);
        Room room = roomService.findById(rid).orElse(null);
        meetingUser.setMeeting(meeting);
        meetingUser.setUser(user);
        meetingUser.setRoom(room);
        meetingUserService.save(meetingUser);
        return Response.success();
    }

    @PostMapping("/meeting")
    @Transactional
    public Response addMeeting(
            @RequestParam("start_time") Timestamp st,
            @RequestParam("end_time") Timestamp et,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("state") String state
    ) {
        Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setStartTime(st);
        meeting.setEndTime(et);
        meeting.setState(state);
        meeting.setType(type);
        meetingService.save(meeting);
        return Response.success();
    }
}