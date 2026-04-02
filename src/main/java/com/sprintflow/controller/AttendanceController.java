package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.AttendanceDTO;
import com.sprintflow.security.JwtTokenProvider;
import com.sprintflow.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AttendanceController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    // POST /api/attendance/submit  (TRAINER only)
    // Body: { sprintId, attendanceDate, records: [{employeeId, status, checkInTime, notes}] }
    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDTO<String>> submit(
            @RequestBody AttendanceDTO.SubmitRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        attendanceService.submitAttendance(request, userId);
        return ok("Attendance submitted successfully", null);
    }

    // GET /api/attendance?sprintId=1&date=2026-03-20
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AttendanceDTO>>> getByDate(
            @RequestParam Long sprintId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ok("Attendance records retrieved successfully",
                attendanceService.getByDate(sprintId, date));
    }

    // GET /api/attendance/stats?sprintId=1
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<List<AttendanceDTO.StatsDTO>>> getStats(
            @RequestParam Long sprintId) {
        return ok("Attendance statistics retrieved successfully",
                attendanceService.getStats(sprintId));
    }

    // GET /api/attendance/cohort-stats?sprintId=1
    @GetMapping("/cohort-stats")
    public ResponseEntity<ApiResponseDTO<List<AttendanceDTO.CohortStatsDTO>>> getCohortStats(
            @RequestParam Long sprintId) {
        return ok("Cohort attendance statistics retrieved successfully",
                attendanceService.getCohortStats(sprintId));
    }

    // ── Helpers ──────────────────────────────────────────────

    private Long getUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
