package com.animerec.chat.controllers;

import com.animerec.chat.services.WorkImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:80" }, allowCredentials = "true")
public class ImportController {

    private final WorkImportService workImportService;

    @PostMapping("/works")
    public ResponseEntity<String> importWorks(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        int count = workImportService.importWorks(file);
        return ResponseEntity.ok("Imported " + count + " works.");
    }
}
