package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    //    Optional<ConversationEntity> findByUser1AndUser2(UserEntity user1, UserEntity user2);
    @Query("""
                SELECT c FROM ConversationEntity c 
                WHERE ((c.user1 = :user1 AND c.user2 = :user2) 
                    OR (c.user1 = :user2 AND c.user2 = :user1)) 
                  AND c.isActive = true
            """)
    Optional<ConversationEntity> findByUser1AndUser2(
            @Param("user1") UserEntity user1,
            @Param("user2") UserEntity user2
    );


    Optional<ConversationEntity> findByUser1AndUser2OrUser2AndUser1(UserEntity user1, UserEntity user2, UserEntity user22, UserEntity user11);


}
