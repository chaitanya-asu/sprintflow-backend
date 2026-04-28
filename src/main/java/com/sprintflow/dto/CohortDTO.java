package com.sprintflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortDTO {
    private Long id;
    private String name;
    private String patternType;
    private String status;
    private LocalDateTime createdAt;
}
