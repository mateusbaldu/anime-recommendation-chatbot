package com.animerec.chat.services;

import com.animerec.chat.dto.response.WorkDetailResponse;
import com.animerec.chat.dto.response.WorkSummaryResponse;
import com.animerec.chat.enums.WorkType;
import com.animerec.chat.models.Work;
import com.animerec.chat.repositories.WorkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkService {

    private final WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public Page<WorkSummaryResponse> search(String query, WorkType type, Pageable pageable) {
        if (type != null) {
            return workRepository.findByMediaType(type, pageable)
                    .map(this::toSummaryResponse);
        }
        
        if (query != null && !query.isBlank()) {
            return workRepository.findByTitleContainingIgnoreCase(query).stream()
                    .map(this::toSummaryResponse)
                    .collect(
                        () -> Page.empty(pageable),
                        (page, item) -> {},
                        (p1, p2) -> {}
                    );
        }
        
        return workRepository.findAll(pageable)
                .map(this::toSummaryResponse);
    }

    public WorkDetailResponse getById(UUID id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work not found"));
        
        return toDetailResponse(work);
    }

    private WorkSummaryResponse toSummaryResponse(Work work) {
        return new WorkSummaryResponse(
                work.getId(),
                work.getTitle(),
                work.getTitleEnglish(),
                work.getMediaType(),
                work.getExternalScore(),
                work.getPopularityCount(),
                work.getGenres()
        );
    }

    private WorkDetailResponse toDetailResponse(Work work) {
        return new WorkDetailResponse(
                work.getId(),
                work.getMalId(),
                work.getTitle(),
                work.getTitleEnglish(),
                work.getSynopsis(),
                work.getMediaType(),
                work.getExternalScore(),
                work.getPopularityCount(),
                work.getGenres(),
                work.getThemes(),
                work.getCreatedAt()
        );
    }
}
