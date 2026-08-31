package com.iitm.hosteldine.dto.mess;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class StudentAttendance {
    private String studentId;
    private String studentName;
    private String messHead;
    private String messName;
    private int dinedDays;
    private Map<Date, DayAttendance> attendanceMap = new HashMap<>();

    public static class DayAttendance {
        private int breakfastCount;
        private int lunchCount;
        private int dinnerCount;

        public DayAttendance(int breakfastCount, int lunchCount, int dinnerCount) {
            this.breakfastCount = breakfastCount;
            this.lunchCount = lunchCount;
            this.dinnerCount = dinnerCount;
        }

        // Getters
        public int getBreakfastCount() {
            return breakfastCount;
        }

        public int getLunchCount() {
            return lunchCount;
        }

        public int getDinnerCount() {
            return dinnerCount;
        }
    }
}