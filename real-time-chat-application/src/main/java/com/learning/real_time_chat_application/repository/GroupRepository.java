package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.GroupEntity;
import com.learning.real_time_chat_application.projection.dto.GroupDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    @Query("SELECT new com.learning.real_time_chat_application.projection.dto.GroupDTO(g.id, g.groupName, COUNT(gm.id)) " +
            "FROM GroupEntity g " +
            "LEFT JOIN GroupMemberEntity gm ON g.id = gm.group.id " +
            "GROUP BY g.id, g.groupName")
    List<GroupDTO> getAllGroupsWithMemberCount();

    @Query("""
                SELECT new com.learning.real_time_chat_application.projection.dto.GroupDTO(
                    g.id, g.groupName, COUNT(gm.user.id)
                ) 
                FROM GroupEntity g
                JOIN GroupMemberEntity gm ON g.id = gm.group.id
                WHERE gm.user.id = :userId
                GROUP BY g.id, g.groupName
            """)
    List<GroupDTO> findGroupsByUserId(@Param("userId") Long userId);
}
