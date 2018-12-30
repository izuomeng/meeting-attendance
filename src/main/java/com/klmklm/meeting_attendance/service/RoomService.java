package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomService extends JpaRepository<Room, Integer> {
}
