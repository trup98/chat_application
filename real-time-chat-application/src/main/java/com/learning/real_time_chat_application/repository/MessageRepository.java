package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.MessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessagesEntity, Long> {
}
