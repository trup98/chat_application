package com.learning.real_time_chat_application.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conversation_master")
@ToString
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_1", nullable = false)
    private UserEntity user1;

    @ManyToOne
    @JoinColumn(name = "user_2", nullable = false)
    private UserEntity user2;



}