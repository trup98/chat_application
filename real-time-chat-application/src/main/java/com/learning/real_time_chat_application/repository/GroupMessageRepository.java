package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.GroupEntity;
import com.learning.real_time_chat_application.entity.GroupMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessageEntity, Long> {

    List<GroupMessageEntity> findByGroupOrderByTimestampAsc(GroupEntity group);

}
