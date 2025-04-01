package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.GroupEntity;
import com.learning.real_time_chat_application.entity.GroupMemberEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {

    List<GroupMemberEntity> findByGroupAndIsActiveTrue(GroupEntity group);

    boolean existsByGroupAndUser(GroupEntity group, UserEntity user);

    void deleteByGroupAndUser(GroupEntity group, UserEntity user);

    List<GroupMemberEntity> findByGroupId(Long groupId);

    Optional<GroupMemberEntity> findByGroupAndUser(GroupEntity group, UserEntity user);

    @Query("SELECT gm.user FROM GroupMemberEntity gm WHERE gm.group.id = :groupId")
    List<UserEntity> findUsersByGroupId(@Param("groupId") Long groupId);


}
