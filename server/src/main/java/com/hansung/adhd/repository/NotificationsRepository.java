package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByParentIdAndIsDeletedFalseOrderByCreatedAtDesc(Long parentId);
}
