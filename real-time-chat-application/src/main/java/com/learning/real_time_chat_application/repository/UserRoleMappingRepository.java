package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.entity.UserRoleMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMappingEntity, Long> {
    Optional<UserRoleMappingEntity> findByUserId(UserEntity userEntity);
}

