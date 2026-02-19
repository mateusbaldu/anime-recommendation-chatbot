package com.animerec.chat.services;

import com.animerec.chat.dto.request.UserProfileUpdateRequest;
import com.animerec.chat.dto.request.UserReviewRequest;
import com.animerec.chat.dto.request.UserWorkStatusRequest;
import com.animerec.chat.dto.response.RecommendationResponse;
import com.animerec.chat.dto.response.UserResponse;
import com.animerec.chat.dto.response.UserReviewResponse;
import com.animerec.chat.dto.response.UserWorkStatusResponse;
import com.animerec.chat.dto.response.WorkSummaryResponse;
import com.animerec.chat.models.*;
import com.animerec.chat.repositories.*;
import com.animerec.chat.security.AuthenticationProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;
    private final UserWorkStatusRepository userWorkStatusRepository;
    private final WorkRepository workRepository;
    private final DataSourceRepository dataSourceRepository;
    private final RecommendationRepository recommendationRepository;
    private final AuthenticationProvider authenticationProvider;

    public UserService(
            UserRepository userRepository,
            UserReviewRepository userReviewRepository,
            UserWorkStatusRepository userWorkStatusRepository,
            WorkRepository workRepository,
            DataSourceRepository dataSourceRepository,
            RecommendationRepository recommendationRepository,
            AuthenticationProvider authenticationProvider) {
        this.userRepository = userRepository;
        this.userReviewRepository = userReviewRepository;
        this.userWorkStatusRepository = userWorkStatusRepository;
        this.workRepository = workRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.recommendationRepository = recommendationRepository;
        this.authenticationProvider = authenticationProvider;
    }

    private UUID getCurrentUserId() {
        return authenticationProvider.getCurrentUserId();
    }

    public UserResponse getProfile() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getProfileImage(),
                user.getCreatedAt(),
                user.getConsentedAt()
        );
    }

    @Transactional
    public UserResponse updateProfile(UserProfileUpdateRequest request) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.profileImage() != null) {
            user.setProfileImage(request.profileImage());
        }
        
        user = userRepository.save(user);
        
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getProfileImage(),
                user.getCreatedAt(),
                user.getConsentedAt()
        );
    }

    @Transactional
    public void deleteAccount() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public Page<UserReviewResponse> getReviews(Pageable pageable) {
        UUID userId = getCurrentUserId();
        return userReviewRepository.findByUserId(userId, pageable)
                .map(this::toReviewResponse);
    }

    @Transactional
    public UserReviewResponse createReview(UserReviewRequest request) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DataSource source = dataSourceRepository.findByName(request.sourceName())
                .orElseThrow(() -> new RuntimeException("Data source not found: " + request.sourceName()));
        
        UserReview review = new UserReview();
        review.setUser(user);
        review.setNormalizedScore(request.normalizedScore());
        review.setReviewText(request.reviewText());
        review.setReviewedAt(request.reviewedAt());
        review.setSource(source);
        
        if (request.workId() != null) {
            Work work = workRepository.findById(request.workId())
                    .orElseThrow(() -> new RuntimeException("Work not found"));
            review.setWork(work);
        } else {
            review.setExternalTitle(request.externalTitle());
        }
        
        review = userReviewRepository.save(review);
        return toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        UUID userId = getCurrentUserId();
        UserReview review = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        userReviewRepository.delete(review);
    }

    public void importReviews(MultipartFile file, String sourceName) {
        throw new UnsupportedOperationException("CSV import not implemented yet - requires parsing logic");
    }

    public List<UserWorkStatusResponse> getWatchStatuses() {
        UUID userId = getCurrentUserId();
        return userWorkStatusRepository.findByIdUserId(userId).stream()
                .map(this::toWorkStatusResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserWorkStatusResponse upsertWatchStatus(UserWorkStatusRequest request) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Work work = workRepository.findById(request.workId())
                .orElseThrow(() -> new RuntimeException("Work not found"));
        
        UserWorkStatusId id = new UserWorkStatusId(userId, request.workId());
        UserWorkStatus status = userWorkStatusRepository.findById(id)
                .orElse(new UserWorkStatus());
        
        status.setId(id);
        status.setUser(user);
        status.setWork(work);
        status.setStatus(request.status());
        
        status = userWorkStatusRepository.save(status);
        return toWorkStatusResponse(status);
    }

    @Transactional
    public void deleteWatchStatus(UUID workId) {
        UUID userId = getCurrentUserId();
        UserWorkStatusId id = new UserWorkStatusId(userId, workId);
        
        if (!userWorkStatusRepository.existsById(id)) {
            throw new RuntimeException("Watch status not found");
        }
        
        userWorkStatusRepository.deleteById(id);
    }

    public Page<RecommendationResponse> getRecommendations(Pageable pageable) {
        UUID userId = getCurrentUserId();
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toRecommendationResponse);
    }

    private UserReviewResponse toReviewResponse(UserReview review) {
        return new UserReviewResponse(
                review.getId(),
                review.getWork() != null ? review.getWork().getId() : null,
                review.getWork() != null ? review.getWork().getTitle() : null,
                review.getExternalTitle(),
                review.getSource().getName(),
                review.getNormalizedScore(),
                review.getReviewText(),
                review.getReviewedAt(),
                review.getCreatedAt()
        );
    }

    private UserWorkStatusResponse toWorkStatusResponse(UserWorkStatus status) {
        return new UserWorkStatusResponse(
                status.getWork().getId(),
                status.getWork().getTitle(),
                status.getStatus(),
                status.getUpdatedAt()
        );
    }

    private RecommendationResponse toRecommendationResponse(Recommendation rec) {
        Work work = rec.getWork();
        WorkSummaryResponse workSummary = new WorkSummaryResponse(
                work.getId(),
                work.getTitle(),
                work.getTitleEnglish(),
                work.getMediaType(),
                work.getExternalScore(),
                work.getPopularityCount(),
                work.getGenres()
        );
        
        return new RecommendationResponse(
                rec.getId(),
                workSummary,
                rec.getReason(),
                rec.getDiversityFlag(),
                rec.getChatSession() != null ? rec.getChatSession().getId() : null,
                rec.getCreatedAt()
        );
    }
}
