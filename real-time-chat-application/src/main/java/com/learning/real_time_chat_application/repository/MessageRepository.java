package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.MessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessagesEntity, Long> {

    List<MessagesEntity> findByConversationId(Long conversationId);

    @Query("SELECT m FROM MessagesEntity m WHERE m.conversation.id = :conversationId ORDER BY m.timestamp ASC")
    List<MessagesEntity> findByConversationIdSorted(@Param("conversationId") Long conversationId);
}
