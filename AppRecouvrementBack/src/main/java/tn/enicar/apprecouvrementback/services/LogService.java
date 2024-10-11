package tn.enicar.apprecouvrementback.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "src/main/resources/logs/Logs.txt";
    private static final String HEADER = "Action,Time,Status,Error Message,Duration,Details\n";

    public LogService() {
        initializeLogFile();
    }

    private void initializeLogFile() {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(HEADER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void logAction(String action, String status, String errorMessage, long duration, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Format the log entry as plain text with newlines and indentation
        String logEntry = String.format(
                "Action: %s\nTime: %s\nStatus: %s\nError Message: %s\nDuration: %d ms\nDetails: %s\n\n",
                action, timestamp, status, errorMessage == null ? "" : errorMessage, duration, details);

        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.append(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
