package com.iozturk.log_analyzer;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class LogAnalyzerService {

    public LogReport analyze(MultipartFile file) throws Exception {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> infos = new ArrayList<>();
        Map<String, Integer> hourlyErrors = new TreeMap<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            String upper = line.toUpperCase();

            if (upper.contains("ERROR")) {
                errors.add(line);
                String hour = extractHour(line);
                if (hour != null) {
                    hourlyErrors.merge(hour, 1, Integer::sum);
                }
            } else if (upper.contains("WARN")) {
                warnings.add(line);
            } else if (upper.contains("INFO")) {
                infos.add(line);
            }
        }

        return new LogReport(
                errors.size(),
                warnings.size(),
                infos.size(),
                errors.stream().limit(10).toList(),
                hourlyErrors
        );
    }

    private String extractHour(String line) {
        try {
            for (String part : line.split(" ")) {
                if (part.matches("\\d{2}:\\d{2}:\\d{2}.*")) {
                    return part.substring(0, 2) + ":00";
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public record LogReport(
            int errorCount,
            int warningCount,
            int infoCount,
            List<String> recentErrors,
            Map<String, Integer> hourlyErrors
    ) {}
}