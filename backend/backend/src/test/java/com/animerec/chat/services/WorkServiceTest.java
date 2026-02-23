package com.animerec.chat.services;

import com.animerec.chat.dto.response.WorkDetailResponse;
import com.animerec.chat.dto.response.WorkSummaryResponse;
import com.animerec.chat.enums.WorkType;
import com.animerec.chat.models.Work;
import com.animerec.chat.repositories.WorkRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkServiceTest {

    @Mock
    private WorkRepository workRepository;

    @InjectMocks
    private WorkService workService;

    private Work mockWork;

    @BeforeEach
    void setUp() {
        mockWork = new Work();
        mockWork.setId(UUID.randomUUID());
        mockWork.setMalId(1);
        mockWork.setTitle("Naruto");
        mockWork.setTitleEnglish("Naruto");
        mockWork.setSynopsis("Ninja story");
        mockWork.setMediaType(WorkType.ANIME);
        mockWork.setExternalScore(BigDecimal.valueOf(8.5));
        mockWork.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void search_ByTypeOnly_ReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Work> page = new PageImpl<>(List.of(mockWork));

        when(workRepository.findByMediaType(WorkType.ANIME, pageable)).thenReturn(page);

        Page<WorkSummaryResponse> result = workService.search(null, WorkType.ANIME, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Naruto", result.getContent().get(0).title());
    }

    @Test
    void search_ByQueryOnly_ReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Work> results = List.of(mockWork);

        when(workRepository.findByTitleContainingIgnoreCase("Naruto")).thenReturn(results);

        Page<WorkSummaryResponse> result = workService.search("Naruto", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Naruto", result.getContent().get(0).title());
    }

    @Test
    void search_NoQueryOrType_ReturnsAllPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Work> page = new PageImpl<>(List.of(mockWork));

        when(workRepository.findAll(pageable)).thenReturn(page);

        Page<WorkSummaryResponse> result = workService.search(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Naruto", result.getContent().get(0).title());
    }

    @Test
    void search_BlankQueryNoType_ReturnsAllPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Work> page = new PageImpl<>(List.of(mockWork));

        when(workRepository.findAll(pageable)).thenReturn(page);

        Page<WorkSummaryResponse> result = workService.search("   ", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Naruto", result.getContent().get(0).title());
    }

    @Test
    void getById_ValidId_ReturnsDetailResponse() {
        when(workRepository.findById(mockWork.getId())).thenReturn(Optional.of(mockWork));

        WorkDetailResponse response = workService.getById(mockWork.getId());

        assertNotNull(response);
        assertEquals(mockWork.getId(), response.id());
        assertEquals("Naruto", response.title());
        assertEquals("Ninja story", response.synopsis());
    }

    @Test
    void getById_InvalidId_ThrowsException() {
        UUID invalidId = UUID.randomUUID();
        when(workRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> workService.getById(invalidId));
    }
}
