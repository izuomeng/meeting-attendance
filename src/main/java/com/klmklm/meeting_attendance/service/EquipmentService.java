package com.klmklm.meeting_attendance.service;

import com.klmklm.meeting_attendance.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentService extends JpaRepository<Equipment, Integer> {
}
