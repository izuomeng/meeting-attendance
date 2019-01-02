package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.Meeting;
import com.klmklm.meeting_attendance.entity.MeetingUser;
import com.klmklm.meeting_attendance.entity.Room;
import com.klmklm.meeting_attendance.entity.User;
import com.klmklm.meeting_attendance.enums.MeetingType;
import com.klmklm.meeting_attendance.lib.ListResponse;
import com.klmklm.meeting_attendance.lib.MyException;
import com.klmklm.meeting_attendance.service.MeetingService;
import com.klmklm.meeting_attendance.service.MeetingUserService;
import com.klmklm.meeting_attendance.service.RoomService;
import com.klmklm.meeting_attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingUserService meetingUserService;
    private final UserService userService;
    private final RoomService roomService;

    @Autowired
    public MeetingController(
            MeetingService meetingService,
            MeetingUserService meetingUserService,
            UserService userService,
            RoomService roomService
    ) {
        this.meetingService = meetingService;
        this.meetingUserService = meetingUserService;
        this.userService = userService;
        this.roomService = roomService;
    }

    private List<Meeting> getCustomMeeting(Predicate<Meeting> func) {
        List<Meeting> meetings = meetingService.findAll();

        return meetings
                .stream()
                .filter(func)
                .collect(Collectors.toList());
    }

    // 获取会议
    @GetMapping("/meetings")
    public List<Meeting> getAllMeetings(
            @RequestParam(required = false, defaultValue = "all") MeetingType type
    ) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        switch (type) {
            case all:
                return meetingService.findAll();
            case ongoing:
                return getCustomMeeting(
                        meeting -> now.before(meeting.getEndTime())
                                && now.after(meeting.getStartTime())
                );
            case finished:
                return getCustomMeeting(meeting -> now.after(meeting.getEndTime()));
            case not_started:
                return getCustomMeeting(meeting -> now.before(meeting.getStartTime()));
            default:
                return meetingService.findAll();
        }

    }

    // 获取某个会议的详细信息
    @GetMapping("/meeting/{id}")
    public Map<String, Object> getMeetingDetail(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        Meeting meeting = meetingService.findById(id).orElse(null);
        BeanMap beanMap = BeanMap.create(meeting);
        List<MeetingUser> meetingUsers = meetingUserService.findAllSignedUser(id);
        List<Map<String, Object>> list = meetingUsers.stream().map(meetingUser -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", meetingUser.getId());
            map.put("userName", meetingUser.getUser().getName());
            map.put("roomName", meetingUser.getRoom().getRoomName());
            return map;
        }).collect(Collectors.toList());

        result.put("users", list);

        for (Object key : beanMap.keySet()) {
            result.put(key + "", beanMap.get(key));
        }

        return result;
    }

    @PostMapping("/meeting")
    @Transactional
    public Object addMeeting(
            @RequestParam("start_time") Timestamp st,
            @RequestParam("end_time") Timestamp et,
            @RequestParam("type") Integer type,
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
        return null;
    }

    // 查看某个的会议报名/签到情况
    @GetMapping("/meeting/{id}/sign")
    public ListResponse<Map<String, Object>> getMeetingSignInfo(
            @PathVariable Integer id,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
            @RequestParam(value = "dimension", required = false, defaultValue = "")
                    String dimension,
            @RequestParam(value = "dimension_id", required = false, defaultValue = "-1")
                    Integer dimensionId
    ) {
        return new ListResponse<>(
                this
                        .meetingUserService
                        .findAllSignedUser(id)
                        .stream()
                        .filter(item -> {
                            // 如果id是room维度，返回对应的会议室的签到信息
                            if (dimension.equals("room")) {
                                return item.getRoom().getId().equals(dimensionId);
                            }
                            return true;
                        })
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
                            result.put("userId",user.getId());
                            result.put("email", user.getEmail());
                            result.put("phone", user.getPhone());
                            result.put("state", item.getState());
                            result.put("meetingRoom", item.getRoom().getRoomName());
                            result.put("signTime", item.getSignTime());
                            result.put("attendance", item.getAttendance());
                            return result;
                        })
                        .collect(Collectors.toList())
        ).success();
    }

    @PostMapping("/meeting/{mid}/sign")
    @Transactional
    public Object addMeetingUser(@PathVariable Integer mid,Integer uid, Integer rid) {
        MeetingUser meetingUser = new MeetingUser();
        Meeting meeting = meetingService.findById(mid).orElse(null);
        User user = userService.findById(uid).orElse(null);
        Room room = roomService.findById(rid).orElse(null);
        meetingUser.setMeeting(meeting);
        meetingUser.setUser(user);
        meetingUser.setRoom(room);
        meetingUserService.save(meetingUser);
        return null;
    }

    @DeleteMapping("/meeting/{mid}/sign")
    @Transactional
    public Object deleteMeetingUser(@PathVariable Integer mid, Integer uid) throws MyException {
//        System.out.println(String.format("%d, %d", mid, uid));
        MeetingUser target = meetingUserService.findMeetingUser(mid, uid);
        if (target == null) {
            throw new MyException("没有该记录", 404);
        }
        meetingUserService.delete(target);
        return null;
    }
}