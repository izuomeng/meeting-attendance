package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingService extends JpaRepository<Meeting, Integer> {
}
