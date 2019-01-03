package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.MeetingRooms;
import com.klmklm.meeting_attendance.entity.MeetingRoomMultiKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomService extends JpaRepository<MeetingRooms, MeetingRoomMultiKeys> {

}
