package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserService extends JpaRepository<User, Integer> {
}
