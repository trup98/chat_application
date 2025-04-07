package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.MessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessagesEntity, Long> {


    @Query("SELECT m FROM MessagesEntity m WHERE m.conversation.id = :conversationId AND m.isActive = true ORDER BY m.timestamp ASC")
    List<MessagesEntity> findByConversationIdSorted(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM MessagesEntity m WHERE m.conversation.id = :conversationId AND " +
            "((m.senderId.id = :userId AND m.isDeletedForSender = false) OR " +
            "(m.receiverId.id = :userId AND m.isDeletedForReceiver = false)) " +
            "ORDER BY m.timestamp ASC")
    List<MessagesEntity> findVisibleMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

}
