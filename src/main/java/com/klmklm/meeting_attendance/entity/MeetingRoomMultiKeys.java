package com.klmklm.meeting_attendance.entity;

import java.io.Serializable;
import java.util.Objects;

public class MeetingRoomMultiKeys implements Serializable {
    private Integer mid;
    private Integer rid;

    public MeetingRoomMultiKeys(Integer mid, Integer rid){
        this.mid = mid;
        this.rid = rid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingRoomMultiKeys that = (MeetingRoomMultiKeys) o;
        return Objects.equals(mid, that.mid) &&
                Objects.equals(rid, that.rid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid, rid);
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }
}
