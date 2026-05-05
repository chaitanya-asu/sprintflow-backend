package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.MasterData;
import com.sprintflow.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/master-data")
public class MasterDataController {

    @Autowired
    private MasterDataRepository masterDataRepository;

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponseDTO<List<String>>> getValues(
            @PathVariable String category,
            @RequestParam(required = false) String parent) {
        
        List<MasterData> list;
        if (parent != null) {
            list = masterDataRepository.findByCategoryAndParentValue(category, parent);
        } else {
            list = masterDataRepository.findByCategory(category);
        }
        
        List<String> values = list.stream().map(MasterData::getValue).collect(Collectors.toList());
        return ok("Data retrieved", values);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<MasterData>>> getAll() {
        return ok("All master data retrieved", masterDataRepository.findAll());
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
