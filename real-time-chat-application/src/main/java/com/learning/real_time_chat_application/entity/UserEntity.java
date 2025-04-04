package com.learning.real_time_chat_application.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_master")
@ToString
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "user_profile_s3_url")
    private String userProfileS3Url;

}
