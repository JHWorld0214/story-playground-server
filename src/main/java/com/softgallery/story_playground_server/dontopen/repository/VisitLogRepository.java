package com.softgallery.story_playground_server.dontopen.repository;

import com.softgallery.story_playground_server.dontopen.domain.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
}
