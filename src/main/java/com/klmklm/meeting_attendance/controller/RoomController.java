package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.Meeting;
import com.klmklm.meeting_attendance.entity.MeetingRoomMultiKeys;
import com.klmklm.meeting_attendance.entity.MeetingRooms;
import com.klmklm.meeting_attendance.entity.MeetingUser;
import com.klmklm.meeting_attendance.lib.ListResponse;
import com.klmklm.meeting_attendance.lib.MyException;
import com.klmklm.meeting_attendance.service.MeetingRoomService;
import com.klmklm.meeting_attendance.service.MeetingService;
import com.klmklm.meeting_attendance.service.MeetingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RoomController {
    private final MeetingUserService meetingUserService;
    private final MeetingService meetingService;
    private final MeetingRoomService meetingRoomService;

    @Autowired
    RoomController(
            MeetingUserService meetingUserService,
            MeetingService meetingService,
            MeetingRoomService meetingRoomService
    ) {
        this.meetingUserService = meetingUserService;
        this.meetingService = meetingService;
        this.meetingRoomService = meetingRoomService;
    }

    private String getSignDescription(Timestamp targetTime, Timestamp realTime) {
        if (realTime == null) {
            return "未到场";
        }
        long targetTimestamp = targetTime.getTime();
        long realTimestamp = realTime.getTime();
        if (realTimestamp > targetTimestamp) {
            long sub = (realTimestamp - targetTimestamp) / 60;
            return String.format("迟到%d分钟", sub);
        } else {
            return "准时到场";
        }
    }

    @GetMapping("/rooms")
    public ListResponse<Map<String, Object>> getMeetingRooms(Integer mid) throws MyException {
        Meeting meeting = meetingService
                .findById(mid)
                .orElseThrow(() -> new MyException("会议不存在", 404));
        return new ListResponse<>(
                meeting.getRooms().stream()
                        .map(room -> {
                            Map<String, Object> map = new HashMap<>();
                            MeetingRoomMultiKeys mr = new MeetingRoomMultiKeys(mid, room.getId());
                            MeetingRooms meetingRoom = meetingRoomService
                                    .findById(mr)
                                    .orElse(null);
                            List<MeetingUser> meetingRoomUsers = meetingUserService
                                    .findAllSignedUser(mid, room.getId());
                            map.put("id", room.getId());
                            map.put("roomName", room.getRoomName());
                            map.put("roomLocation", room.getLocation());
                            map.put("signNum", meetingRoomUsers.size());
                            map.put("signTime", meeting.getSignTime());
                            if (meetingRoom != null && meetingRoom.getSignTime() != null) {
                                map.put("signTime", meetingRoom.getSignTime());
                            }
                            return map;
                        })
                        .collect(Collectors.toList())
        ).success();
    }

    @GetMapping("/room/sign")
    public ListResponse<Map<String, Object>> getMeetingStatistics(
            Integer mid,
            Integer rid
    ) throws MyException {
        List<MeetingUser> meetingRoomUsers = meetingUserService.findAllSignedUser(mid, rid);
        Meeting meeting =
                meetingService.findById(mid).orElseThrow(() -> new MyException("会议不存在", 404));
        return new ListResponse<>(
                meetingRoomUsers.stream()
                        .map(meetingUser -> {
                            Map<String, Object> result = new HashMap<>();
                            Timestamp targetSignTime = Optional.ofNullable(meeting.getSignTime())
                                    .orElse(meeting.getStartTime());
                            result.put("userName", meetingUser.getUser().getName());
                            result.put("signTime", meetingUser.getSignTime());
                            result.put("signDesc", getSignDescription(
                                    targetSignTime,
                                    meetingUser.getSignTime()
                            ));
                            result.put("cameraName", "waiting...");
                            result.put("image", meetingUser.getImage());
                            return result;
                        })
                        .collect(Collectors.toList())
        ).success();
    }
}
