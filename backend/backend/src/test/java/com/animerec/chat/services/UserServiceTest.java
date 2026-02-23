package com.animerec.chat.services;

import com.animerec.chat.dto.request.UserProfileUpdateRequest;
import com.animerec.chat.dto.request.UserReviewRequest;
import com.animerec.chat.dto.request.UserWorkStatusRequest;
import com.animerec.chat.dto.response.RecommendationResponse;
import com.animerec.chat.dto.response.UserResponse;
import com.animerec.chat.dto.response.UserReviewResponse;
import com.animerec.chat.dto.response.UserWorkStatusResponse;
import com.animerec.chat.enums.WatchStatus;
import com.animerec.chat.enums.WorkType;
import com.animerec.chat.models.Recommendation;
import com.animerec.chat.models.User;
import com.animerec.chat.models.UserReview;
import com.animerec.chat.models.UserWorkStatus;
import com.animerec.chat.models.UserWorkStatusId;
import com.animerec.chat.models.Work;
import com.animerec.chat.models.DataSource;
import com.animerec.chat.repositories.DataSourceRepository;
import com.animerec.chat.repositories.RecommendationRepository;
import com.animerec.chat.repositories.UserRepository;
import com.animerec.chat.repositories.UserReviewRepository;
import com.animerec.chat.repositories.UserWorkStatusRepository;
import com.animerec.chat.repositories.WorkRepository;
import com.animerec.chat.security.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReviewRepository userReviewRepository;

    @Mock
    private UserWorkStatusRepository userWorkStatusRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private DataSourceRepository dataSourceRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private WorkImportService workImportService;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Work mockWork;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(currentUserId);
        mockUser.setEmail("test@example.com");
        mockUser.setDisplayName("Test User");
        mockUser.setGuestSessionId("guest-123");

        mockWork = new Work();
        mockWork.setId(UUID.randomUUID());
        mockWork.setTitle("Test Anime");
        mockWork.setMediaType(WorkType.ANIME);

        // lenient().when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
    }

    @Test
    void getOrCreateGuestUser_ExistingUser_ReturnsUser() {
        when(userRepository.findByGuestSessionId("guest-123")).thenReturn(Optional.of(mockUser));
        
        User result = userService.getOrCreateGuestUser("guest-123");
        
        assertNotNull(result);
        assertEquals(currentUserId, result.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getOrCreateGuestUser_NewUser_CreatesAndReturnsUser() {
        when(userRepository.findByGuestSessionId("new-guest")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        
        User result = userService.getOrCreateGuestUser("new-guest");
        
        assertNotNull(result);
        assertEquals("new-guest", result.getGuestSessionId());
        assertTrue(result.getDisplayName().startsWith("Guest"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getProfile_ReturnsUserResponse() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(mockUser));
        
        UserResponse response = userService.getProfile();
        
        assertNotNull(response);
        assertEquals(currentUserId, response.id());
        assertEquals("Test User", response.displayName());
    }

    @Test
    void updateProfile_ValidRequest_UpdatesAndReturnsUser() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("Updated Name", "http://new-image.com");
        
        UserResponse response = userService.updateProfile(request);
        
        assertNotNull(response);
        assertEquals("Updated Name", response.displayName());
        assertEquals("http://new-image.com", response.profileImage());
    }

    @Test
    void createReview_ValidRequest_CreatesReview() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(mockUser));
        when(workRepository.findById(mockWork.getId())).thenReturn(Optional.of(mockWork));
        
        DataSource mockSource = new DataSource();
        mockSource.setName("MyAnimeList");
        when(dataSourceRepository.findByName("MyAnimeList")).thenReturn(Optional.of(mockSource));
        
        UserReview mockReview = new UserReview();
        mockReview.setId(UUID.randomUUID());
        mockReview.setUser(mockUser);
        mockReview.setWork(mockWork);
        mockReview.setNormalizedScore(BigDecimal.valueOf(8));
        mockReview.setReviewText("Great anime");
        mockReview.setCreatedAt(LocalDateTime.now());
        mockReview.setSource(mockSource);
        
        when(userReviewRepository.save(any(UserReview.class))).thenReturn(mockReview);
        
        UserReviewRequest request = new UserReviewRequest(
                mockWork.getId(), 
                null, 
                "MyAnimeList", 
                BigDecimal.valueOf(8), 
                "Great anime", 
                LocalDateTime.now()
        );
        UserReviewResponse response = userService.createReview(request);
        
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(8), response.normalizedScore());
        assertEquals("Great anime", response.reviewText());
        assertEquals(mockWork.getId(), response.workId());
    }

    @Test
    void upsertWatchStatus_CreatesNewStatus() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(mockUser));
        when(workRepository.findById(mockWork.getId())).thenReturn(Optional.of(mockWork));
        when(userWorkStatusRepository.findById(any())).thenReturn(Optional.empty());
        
        UserWorkStatus status = new UserWorkStatus();
        status.setId(new UserWorkStatusId(currentUserId, mockWork.getId()));
        status.setUser(mockUser);
        status.setWork(mockWork);
        status.setStatus(WatchStatus.WATCHING);
        
        when(userWorkStatusRepository.save(any(UserWorkStatus.class))).thenReturn(status);
        
        UserWorkStatusRequest request = new UserWorkStatusRequest(mockWork.getId(), WatchStatus.WATCHING);
        UserWorkStatusResponse response = userService.upsertWatchStatus(request);
        
        assertNotNull(response);
        assertEquals(WatchStatus.WATCHING, response.status());
        assertEquals(mockWork.getId(), response.workId());
    }

    @Test
    void getRecommendations_ReturnsPagedResponses() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        
        Recommendation rec = new Recommendation();
        rec.setId(UUID.randomUUID());
        rec.setUser(mockUser);
        rec.setWork(mockWork);
        rec.setReason("Because you liked...");
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> page = new PageImpl<>(List.of(rec));
        
        when(recommendationRepository.findByUserIdOrderByCreatedAtDesc(currentUserId, pageable)).thenReturn(page);
        
        Page<RecommendationResponse> responses = userService.getRecommendations(pageable);
        
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals("Because you liked...", responses.getContent().get(0).reason());
    }
}
