package com.sprintflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDTO {
    private String roomName;
    private List<BookingDTO> bookings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDTO {
        private Long sprintId;
        private String sprintTitle;
        private String trainerName;
        private String startDate;
        private String endDate;
        private String startTime;
        private String endTime;
    }
}
