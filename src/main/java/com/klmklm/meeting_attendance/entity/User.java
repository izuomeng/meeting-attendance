package com.klmklm.meeting_attendance.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String face;
//    @OneToMany(mappedBy = "user")
//    private List<MeetingUser> meetingUsers;
//
//    public List<MeetingUser> getMeetingUsers() {
//        return meetingUsers;
//    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFace() {
        return face;
    }
}
