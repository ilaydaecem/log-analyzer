package com.iozturk.log_analyzer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class LogAnalyzerController {

    private final LogAnalyzerService logAnalyzerService;

    public LogAnalyzerController(LogAnalyzerService logAnalyzerService) {
        this.logAnalyzerService = logAnalyzerService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("file") MultipartFile file, Model model) {
        try {
            LogAnalyzerService.LogReport report = logAnalyzerService.analyze(file);
            model.addAttribute("report", report);
            model.addAttribute("filename", file.getOriginalFilename());
        } catch (Exception e) {
            model.addAttribute("error", "Dosya okunamadı: " + e.getMessage());
        }
        return "result";
    }
}