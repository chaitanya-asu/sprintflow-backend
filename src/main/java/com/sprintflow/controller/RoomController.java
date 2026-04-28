package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.RoomDTO;
import com.sprintflow.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Room management for sprint scheduling")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Get all rooms")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RoomDTO>>> getAllRooms() {
        try {
            List<RoomDTO> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(ApiResponseDTO.<List<RoomDTO>>builder()
                    .success(true)
                    .message("Rooms retrieved successfully")
                    .data(rooms)
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.<List<RoomDTO>>builder()
                            .success(false)
                            .message("Failed to retrieve rooms: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    @Operation(summary = "Get room by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RoomDTO>> getRoomById(@PathVariable Long id) {
        try {
            RoomDTO room = roomService.getRoomById(id);
            return ResponseEntity.ok(ApiResponseDTO.<RoomDTO>builder()
                    .success(true)
                    .message("Room retrieved successfully")
                    .data(room)
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(404)
                            .build());
        }
    }

    @Operation(summary = "Get room by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponseDTO<RoomDTO>> getRoomByName(@PathVariable String name) {
        try {
            RoomDTO room = roomService.getRoomByName(name);
            return ResponseEntity.ok(ApiResponseDTO.<RoomDTO>builder()
                    .success(true)
                    .message("Room retrieved successfully")
                    .data(room)
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(404)
                            .build());
        }
    }

    @Operation(summary = "Create a new room")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RoomDTO>> createRoom(@RequestBody RoomDTO roomDTO) {
        try {
            RoomDTO createdRoom = roomService.createRoom(roomDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(true)
                            .message("Room created successfully")
                            .data(createdRoom)
                            .statusCode(201)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(400)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message("Failed to create room: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    @Operation(summary = "Update an existing room")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RoomDTO>> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
        try {
            RoomDTO updatedRoom = roomService.updateRoom(id, roomDTO);
            return ResponseEntity.ok(ApiResponseDTO.<RoomDTO>builder()
                    .success(true)
                    .message("Room updated successfully")
                    .data(updatedRoom)
                    .statusCode(200)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(400)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.<RoomDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(404)
                            .build());
        }
    }

    @Operation(summary = "Delete a room")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                    .success(true)
                    .message("Room deleted successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(404)
                            .build());
        }
    }
}
