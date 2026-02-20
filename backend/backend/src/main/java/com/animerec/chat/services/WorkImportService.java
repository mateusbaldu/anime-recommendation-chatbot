package com.animerec.chat.services;

import com.animerec.chat.enums.WorkType;
import com.animerec.chat.exceptions.CsvImportException;
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
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkImportService {

    private final WorkRepository workRepository;
    private final DataSourceRepository dataSourceRepository;
    private final AIService aiService;

    @Transactional
    public int importWorks(MultipartFile file) {
        AtomicInteger count = new AtomicInteger(0);

        try (Reader reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            csvReaderStream(csvReader)
                    .peek(line -> log.debug("Processing row: {}", (Object) line))
                    .map(this::parseRow)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(work -> workRepository.findByMalId(work.getMalId()).isEmpty())
                    .peek(this::enrichWithEmbedding)
                    .forEach(work -> {
                        workRepository.save(work);
                        count.incrementAndGet();
                    });

        } catch (CsvImportException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to import CSV", e);
            throw new CsvImportException("Failed to import CSV: " + e.getMessage(), e);
        }

        log.info("Completed CSV import. Processed {} records.", count.get());
        return count.get();
    }

    private Stream<String[]> csvReaderStream(CSVReader csvReader) {
        Iterator<String[]> iterator = csvReader.iterator();
        Spliterator<String[]> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false);
    }

    private Optional<Work> parseRow(String[] line) {
        try {
            Integer malId = parseInteger(line[0]);
            String title = line[1];
            String synopsis = line[2];
            String typeStr = line[3];
            BigDecimal score = parseBigDecimal(line[4]);

            Work work = new Work();
            work.setMalId(malId);
            work.setTitle(title);
            work.setSynopsis(synopsis);
            work.setExternalScore(score);

            Optional.ofNullable(typeStr)
                    .map(String::toUpperCase)
                    .flatMap(this::safeParseWorkType)
                    .ifPresent(work::setMediaType);

            return Optional.of(work);
        } catch (Exception e) {
            log.error("Error processing row: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void enrichWithEmbedding(Work work) {
        Optional.ofNullable(work.getSynopsis())
                .filter(s -> !s.isEmpty())
                .map(aiService::getEmbedding)
                .ifPresent(work::setEmbedding);
    }

    private Optional<WorkType> safeParseWorkType(String typeStr) {
        try {
            return Optional.of(WorkType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Integer parseInteger(String val) {
        return Optional.ofNullable(val)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> {
                    try {
                        return Optional.of(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .orElse(null);
    }

    private BigDecimal parseBigDecimal(String val) {
        return Optional.ofNullable(val)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> {
                    try {
                        return Optional.of(new BigDecimal(s));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .orElse(null);
    }
}
