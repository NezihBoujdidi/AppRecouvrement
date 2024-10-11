package tn.enicar.apprecouvrementback.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    private static final String LOG_FILE_PATH = "src/main/resources/logs/Logs.txt"; // Adjust path as needed

    @GetMapping("/details")
    public ResponseEntity<InputStreamResource> getLogFile() throws FileNotFoundException {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            throw new FileNotFoundException("Log file not found");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(logFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Logs.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(logFile.length())
                .body(resource);
    }
}
