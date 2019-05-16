package com.klmklm.meeting_attendance.controller;

import com.klmklm.meeting_attendance.entity.*;
import com.klmklm.meeting_attendance.enums.MeetingType;
import com.klmklm.meeting_attendance.lib.ListResponse;
import com.klmklm.meeting_attendance.lib.MyException;
import com.klmklm.meeting_attendance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // helpers
    private Stream<Map<String, Object>> getCustomMeeting(Predicate<Meeting> func) {
        return meetingService
                .findAll()
                .stream()
                .filter(func)
                .map(meeting -> {
                    Map<String, Object> map = entity2Map(meeting);
                    List<User> users = meetingUserService
                            .findAllSignedUser(meeting.getId())
                            .stream()
                            .map(MeetingUser::getUser)
                            .collect(Collectors.toList());
                    map.put("users", users);
                    return map;
                });
//                .collect(Collectors.toList());
    }

    private List<MeetingUser> getMeetingUsersByState(Integer mid, Integer state) {
        return meetingUserService
                .findAllSignedUser(mid)
                .stream()
                .filter(item -> item.getState().equals(state))
                .collect(Collectors.toList());
    }

    // stack overflow上粘来的，完全不懂啥意思但是可以用
    private Map<String, Object> entity2Map(Object entity) {
        Map<String, Object> result = new HashMap<>();
        if (entity == null) {
            return result;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(entity.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method reader = pd.getReadMethod();
                if (reader != null)
                    result.put(pd.getName(), reader.invoke(entity));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }

    // Mappings

    // 获取会议
    @GetMapping("/meetings")
    public ListResponse<Map<String, Object>> getAllMeetings(
            @RequestParam(required = false, defaultValue = "all") MeetingType type,
            @RequestParam(required = false) Long date,
            @RequestParam(required = false) String title
    ) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Stream<Map<String, Object>> result;

        switch (type) {
            case all:
                result = getCustomMeeting(meeting -> true);
                break;
            case ongoing:
                result = getCustomMeeting(
                        meeting -> now.before(meeting.getEndTime())
                                && now.after(meeting.getStartTime())
                );
                break;
            case finished:
                result = getCustomMeeting(meeting -> now.after(meeting.getEndTime()));
                break;
            case not_started:
                result = getCustomMeeting(meeting -> now.before(meeting.getStartTime()));
                break;
            default:
                result = null;
                break;
        }

        if (result == null) {
            return null;
        }

        if (date != null) {
            result = result.filter(meeting -> {
                String formattedDate =
                        new SimpleDateFormat("yyyy-MM-dd").format(date);
                String meetingDate =
                        new SimpleDateFormat("yyyy-MM-dd")
                                .format(meeting.get("startTime"));
                return formattedDate.equals(meetingDate);
            });
        }

        if (title != null) {
            result = result.filter(meeting -> meeting.get("title").toString().contains(title));
        }

        return new ListResponse<>(result.collect(Collectors.toList())).success();

    }

    // 获取某个会议的详细信息
    @GetMapping("/meeting/{id}")
    public Map<String, Object> getMeetingDetail(@PathVariable Integer id) {
        Meeting meeting = meetingService.findById(id).orElse(null);

        // 注入meeting实体
        Map<String, Object> result = entity2Map(meeting);

        // 注入user信息
        List<MeetingUser> meetingUsers = meetingUserService.findAllSignedUser(id);
        List<Map<String, Object>> list = meetingUsers.stream().map(meetingUser -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", meetingUser.getId());
            map.put("userName", meetingUser.getUser().getName());
            map.put("roomName", meetingUser.getRoom().getRoomName());
            return map;
        }).collect(Collectors.toList());
        result.put("users", list);

        return result;
    }

    @PutMapping("/meeting")
    @Transactional
    public Object editMeeting(@RequestBody Meeting meeting) {
        meetingService.save(meeting);
        return null;
    }

    // 查看某个的会议报名/签到情况
    @GetMapping("/meeting/{id}/sign")
    public ListResponse<Map<String, Object>> r(
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
                            result.put("userId", user.getId());
                            result.put("email", user.getEmail());
                            result.put("phone", user.getPhone());
                            result.put("state", item.getState());
                            result.put("meetingRoom", item.getRoom().getRoomName());
                            result.put("roomLocation", item.getRoom().getLocation());
                            // 注意这个signTime是真实签到时间，不是目标签到时间
                            result.put("signTime", item.getSignTime());
                            result.put("attendance", item.getAttendance());
                            result.put("face", user.getFace());
                            result.put("image", item.getImage());
                            result.put("cameraName", item.getCamera());
                            return result;
                        })
                        .collect(Collectors.toList())
        ).success();
    }

    private MeetingUser createMeetingUser(Integer mid, Integer uid, Integer rid) {
        MeetingUser meetingUser = new MeetingUser();
        Meeting meeting = meetingService.findById(mid).orElse(null);
        User user = userService.findById(uid).orElse(null);
        Room room = roomService.findById(rid).orElse(null);
        meetingUser.setMeeting(meeting);
        meetingUser.setUser(user);
        meetingUser.setRoom(room);
        return meetingUser;
    }

    @PostMapping("/meeting/sign")
    @Transactional
    public Object addMeetingUser(Integer mid, Integer uid, Integer rid) {
        MeetingUser meetingUser = createMeetingUser(mid, uid, rid);
        meetingUserService.save(meetingUser);
        return null;
    }

    @PutMapping("/meeting/sign")
    @Transactional
    public Object updateMeetingUser(Integer id, Integer attendance, Timestamp time, String image, String camera) throws MyException {
        MeetingUser meetingUser = meetingUserService
                .findById(id)
                .orElseThrow(() -> new MyException("Can not find meetingUser", 1));
        meetingUser.setAttendance(attendance);
        meetingUser.setSignTime(time);
        meetingUser.setImage(image);
        meetingUser.setCamera(camera);
        meetingUserService.save(meetingUser);
        return null;
    }

    @DeleteMapping("/meeting/sign")
    @Transactional
    public Object deleteMeetingUser(Integer id) throws MyException {
        MeetingUser target = meetingUserService
                .findById(id)
                .orElseThrow(() -> new MyException("Can not find meetingUser", 1));
        meetingUserService.delete(target);
        return null;
    }

    private Map<String, Object> getMeetingStatistics(Integer mid) throws MyException {
        Map<String, Object> result = new HashMap<>();
        Meeting meeting = meetingService.findById(mid).orElseThrow(() -> new MyException("meeting doesn't exist", 1));
        List<MeetingUser> meetingUsers = meetingUserService.findAllSignedUser(mid);
        List<MeetingUser> confirmUsers = getMeetingUsersByState(mid, 2);
        List<MeetingUser> declineUsers = getMeetingUsersByState(mid, 1);
        List<MeetingUser> unknownUsers = getMeetingUsersByState(mid, 0);
        result.put("total", meetingUsers.size());
        result.put("confirm", confirmUsers.size());
        result.put("decline", declineUsers.size());
        result.put("unknown", unknownUsers.size());
        result.put("title", meeting.getTitle());
        result.put("id", mid);
        return result;
    }

    @GetMapping("/meetings/statistics")
        public List<Map<String, Object>> getAllStatistics() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Stream<Map<String, Object>> finishedMeetings =
                    getCustomMeeting(meeting -> now.after(meeting.getEndTime()));

            return finishedMeetings
                    .map(item -> {
                        try {
                            return getMeetingStatistics((Integer)item.get("id"));
                        } catch (MyException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
    }
    @GetMapping("/meeting/{id}/statistics")
    public Map<String, Object> getAllStatistics(@PathVariable Integer id) throws MyException {
        return getMeetingStatistics(id);
    }
}