package com.animerec.chat.services;

import com.animerec.chat.exceptions.CsvImportException;
import com.animerec.chat.models.Work;
import com.animerec.chat.repositories.DataSourceRepository;
import com.animerec.chat.repositories.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkImportServiceTest {

    @Mock
    private WorkRepository workRepository;

    @Mock
    private DataSourceRepository dataSourceRepository;

    @Mock
    private AIService aiService;

    @InjectMocks
    private WorkImportService workImportService;

    private String validCsvContent;

    @BeforeEach
    void setUp() {
        validCsvContent = "mal_id,title,synopsis,type,score\n" +
                "1,Cowboy Bebop,Space western,TV,8.75\n" +
                "5,Cowboy Bebop: Tengoku no Tobira,Movie,Movie,8.38";
    }

    @Test
    void importWorks_ValidFile_ReturnsImportedCount() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", validCsvContent.getBytes());

        when(workRepository.findByMalId(any())).thenReturn(Optional.empty());
        when(aiService.getEmbedding(anyString())).thenReturn(new float[] { 0.1f, 0.2f });
        when(workRepository.save(any(Work.class))).thenAnswer(i -> i.getArgument(0));

        int count = workImportService.importWorks(file);

        assertEquals(2, count);
        verify(workRepository, times(2)).save(any(Work.class));
        verify(aiService, times(2)).getEmbedding(anyString());
    }

    @Test
    void importWorks_ExistingWork_SkipsExisting() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", validCsvContent.getBytes());

        // First gets skipped (already exists), second gets imported
        lenient().when(workRepository.findByMalId(1)).thenReturn(Optional.of(new Work()));
        lenient().when(workRepository.findByMalId(5)).thenReturn(Optional.empty());

        when(aiService.getEmbedding(anyString())).thenReturn(new float[] { 0.1f, 0.2f });
        when(workRepository.save(any(Work.class))).thenAnswer(i -> i.getArgument(0));

        int count = workImportService.importWorks(file);

        assertEquals(1, count);
        verify(workRepository, times(1)).save(any(Work.class));
    }

    @Test
    void importWorks_MalformedRow_SkipsRowAndContinues() throws Exception {
        String invalidCsvContent = "mal_id,title,synopsis,type,score\n" +
                "invalid_id,Cowboy Bebop,Space western,TV,8.75\n" + // Invalid ID
                "5,Cowboy Bebop: Tengoku no Tobira,Movie,Movie,8.38";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", invalidCsvContent.getBytes());

        lenient().when(workRepository.findByMalId(null)).thenReturn(Optional.empty());
        lenient().when(workRepository.findByMalId(5)).thenReturn(Optional.empty());
        when(aiService.getEmbedding(anyString())).thenReturn(new float[] { 0.1f, 0.2f });
        when(workRepository.save(any(Work.class))).thenAnswer(i -> i.getArgument(0));

        int count = workImportService.importWorks(file);

        assertEquals(2, count);
        verify(workRepository, times(2)).save(any(Work.class));
    }

    @Test
    void importWorks_FileReadError_ThrowsCsvImportException() throws Exception {
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));

        assertThrows(CsvImportException.class, () -> workImportService.importWorks(file));
    }
}
