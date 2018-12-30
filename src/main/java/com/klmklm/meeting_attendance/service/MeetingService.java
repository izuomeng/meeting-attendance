package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingService extends JpaRepository<Meeting, Integer> {
    @Query("select m from Meeting m where m.title = ?1")
    Meeting findByTitle(String title);
}
