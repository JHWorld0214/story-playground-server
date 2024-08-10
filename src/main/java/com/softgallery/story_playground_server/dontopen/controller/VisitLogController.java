package com.softgallery.story_playground_server.dontopen.controller;

import com.softgallery.story_playground_server.dontopen.domain.VisitLog;
import com.softgallery.story_playground_server.dontopen.dto.VisitLogDTO;
import com.softgallery.story_playground_server.dontopen.service.VisitLogService;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/dontopen")
public class VisitLogController {
    private final VisitLogService visitLogService;

    @GetMapping("/visitor")
    public String showVisitLog(Model model, HttpServletResponse response) {
        List<VisitLogDTO> logs = visitLogService.getAllLogs();
        model.addAttribute("logs", logs);

        return "index";

    }

    @PostMapping("/visitor/submit")
    public ResponseEntity<SuccessResponse<?>> makeLog(@RequestParam("name") String name, Model model, HttpServletResponse response) {
        System.out.println("hihi");
        String localDateTime = visitLogService.makeLog(name);
        List<VisitLogDTO> logs = visitLogService.getAllLogs();
        model.addAttribute("name", name);
        model.addAttribute("time", localDateTime);
        model.addAttribute("logs", logs);

        return SuccessResponse.ok(logs);
    }

}
