package com.learning.real_time_chat_application.repository;

import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.projection.GetAllUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByIdAndIsActiveTrue(Long id);


    @Query(nativeQuery = true, value = "SELECT um.id AS id,um.user_name AS userName,um.email AS email FROM learning.user_master um WHERE um.is_active = TRUE " +
            "AND (um.user_name LIKE CONCAT('%',:searchKey,'%') OR um.email LIKE CONCAT('%',:searchKey,'%')) ")
    Page<GetAllUser> findAllUser(Pageable pageable, @Param("searchKey") String searchKey);


    Optional<UserEntity> findByEmail(String username);

}
