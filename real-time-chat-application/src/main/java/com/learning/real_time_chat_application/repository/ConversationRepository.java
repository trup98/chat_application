package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    Optional<ConversationEntity> findByUser1AndUser2(UserEntity user1, UserEntity user2);
}
