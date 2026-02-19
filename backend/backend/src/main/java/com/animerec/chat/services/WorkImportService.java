package com.animerec.chat.services;

import com.animerec.chat.enums.WorkType;
import com.animerec.chat.models.DataSource;
import com.animerec.chat.models.Work;
import com.animerec.chat.repositories.DataSourceRepository;
import com.animerec.chat.repositories.WorkRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkImportService {

    private final WorkRepository workRepository;
    private final DataSourceRepository dataSourceRepository;
    private final AIService aiService;

    @Transactional
    public int importWorks(MultipartFile file) {
        int count = 0;
        try (Reader reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                log.debug("Processing row: {}", (Object) line);
                try {
                    Integer malId = parseInteger(line[0]);
                    String title = line[1];
                    String synopsis = line[2];
                    String typeStr = line[3];
                    BigDecimal score = parseBigDecimal(line[4]);

                    Optional<Work> existing = workRepository.findByMalId(malId);
                    if (existing.isPresent()) {
                        continue;
                    }

                    Work work = new Work();
                    work.setMalId(malId);
                    work.setTitle(title);
                    work.setSynopsis(synopsis);
                    work.setExternalScore(score);

                    if (typeStr != null) {
                        try {
                            work.setMediaType(WorkType.valueOf(typeStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                        }
                    }

                    if (synopsis != null && !synopsis.isEmpty()) {
                        float[] embedding = aiService.getEmbedding(synopsis);
                        work.setEmbedding(embedding);
                    }

                    workRepository.save(work);
                    count++;
                } catch (Exception e) {
                    log.error("Error processing row {}: {}", count, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to import CSV", e);
            throw new RuntimeException("Failed to import CSV: " + e.getMessage());
        }
        log.info("Completed CSV import. Processed {} records.", count);
        return count;
    }

    private Integer parseInteger(String val) {
        if (val == null || val.trim().isEmpty())
            return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String val) {
        if (val == null || val.trim().isEmpty())
            return null;
        try {
            return new BigDecimal(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
