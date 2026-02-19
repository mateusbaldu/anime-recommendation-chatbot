package com.animerec.chat.controllers;

import com.animerec.chat.dto.response.WorkDetailResponse;
import com.animerec.chat.dto.response.WorkSummaryResponse;
import com.animerec.chat.enums.WorkType;
import com.animerec.chat.services.WorkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/works")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    public ResponseEntity<Page<WorkSummaryResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) WorkType type,
            Pageable pageable) {
        return ResponseEntity.ok(workService.search(q, type, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(workService.getById(id));
    }
}
