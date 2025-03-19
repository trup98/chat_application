package com.learning.real_time_chat_application.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages_master")
@ToString
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MessagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiverId;

    @Column(name = "content")
    private String content;

    @Column(name = "message_time")
    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;


}
