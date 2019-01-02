package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.MeetingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingUserService extends JpaRepository<MeetingUser, Integer> {

    @Query("from MeetingUser m where m.meeting.id = ?1")
    List<MeetingUser> findAllSignedUser(Integer meetingId);

    @Query("from MeetingUser m where m.meeting.id = ?1 and m.user.id = ?2")
    MeetingUser findMeetingUser(Integer meetingId, Integer userId);
}
