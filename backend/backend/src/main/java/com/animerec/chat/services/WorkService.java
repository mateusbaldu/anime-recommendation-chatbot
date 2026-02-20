package com.animerec.chat.services;

import com.animerec.chat.dto.response.WorkDetailResponse;
import com.animerec.chat.dto.response.WorkSummaryResponse;
import com.animerec.chat.enums.WorkType;
import com.animerec.chat.models.Work;
import com.animerec.chat.repositories.WorkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkService {

    private final WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public Page<WorkSummaryResponse> search(String query, WorkType type, Pageable pageable) {
        Page<Work> works = Optional.ofNullable(type)
                .map(t -> workRepository.findByMediaType(t, pageable))
                .orElseGet(() -> Optional.ofNullable(query)
                        .filter(q -> !q.isBlank())
                        .<Page<Work>>map(q -> {
                            List<Work> results = workRepository.findByTitleContainingIgnoreCase(q);
                            return new PageImpl<>(results, pageable, results.size());
                        })
                        .orElseGet(() -> workRepository.findAll(pageable)));

        return works.map(this::toSummaryResponse);
    }

    public WorkDetailResponse getById(UUID id) {
        return workRepository.findById(id)
                .map(this::toDetailResponse)
                .orElseThrow(() -> new RuntimeException("Work not found"));
    }

    private WorkSummaryResponse toSummaryResponse(Work work) {
        return new WorkSummaryResponse(
                work.getId(),
                work.getTitle(),
                work.getTitleEnglish(),
                work.getMediaType(),
                work.getExternalScore(),
                work.getPopularityCount(),
                work.getGenres());
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
                work.getCreatedAt());
    }
}
