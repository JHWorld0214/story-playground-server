package com.softgallery.story_playground_server.dontopen.service;

import com.softgallery.story_playground_server.dontopen.domain.VisitLog;
import com.softgallery.story_playground_server.dontopen.dto.VisitLogDTO;
import com.softgallery.story_playground_server.dontopen.repository.VisitLogRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class VisitLogService {
    private final VisitLogRepository visitLogRepository;

    public String makeLog(String name) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        VisitLog visitLog = visitLogRepository.save(
                VisitLog.builder()
                        .name(name==null ? "no name" : name)
                        .localDateTime(formattedDate)
                        .build()
        );

        return visitLog.getLocalDateTime();
    }

    public List<VisitLogDTO> getAllLogs() {
        return visitLogRepository.findAll().stream()
                .map(log -> new VisitLogDTO(log.getName(), log.getLocalDateTime()))
                .collect(Collectors.toList());
    }
}
