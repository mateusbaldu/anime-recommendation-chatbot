package com.animerec.chat.controllers;

import com.animerec.chat.dto.request.UserProfileUpdateRequest;
import com.animerec.chat.dto.request.UserReviewRequest;
import com.animerec.chat.dto.request.UserWorkStatusRequest;
import com.animerec.chat.dto.response.RecommendationResponse;
import com.animerec.chat.dto.response.UserResponse;
import com.animerec.chat.dto.response.UserReviewResponse;
import com.animerec.chat.dto.response.UserWorkStatusResponse;
import com.animerec.chat.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateProfile(@RequestBody @Valid UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount() {
        userService.deleteAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<UserReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(userService.getReviews(pageable));
    }

    @PostMapping("/reviews")
    public ResponseEntity<UserReviewResponse> createReview(@RequestBody @Valid UserReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createReview(request));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        userService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reviews/import")
    public ResponseEntity<Void> importReviews(
            @RequestParam("file") MultipartFile file,
            @RequestParam("source") String sourceName) {
        userService.importReviews(file, sourceName);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/watch-status")
    public ResponseEntity<List<UserWorkStatusResponse>> getWatchStatuses() {
        return ResponseEntity.ok(userService.getWatchStatuses());
    }

    @PutMapping("/watch-status")
    public ResponseEntity<UserWorkStatusResponse> upsertWatchStatus(@RequestBody @Valid UserWorkStatusRequest request) {
        return ResponseEntity.ok(userService.upsertWatchStatus(request));
    }

    @DeleteMapping("/watch-status/{workId}")
    public ResponseEntity<Void> deleteWatchStatus(@PathVariable UUID workId) {
        userService.deleteWatchStatus(workId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Page<RecommendationResponse>> getRecommendations(Pageable pageable) {
        return ResponseEntity.ok(userService.getRecommendations(pageable));
    }
}
