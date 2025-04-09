package com.learning.real_time_chat_application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Date;

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

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private Boolean isActive;

    @Column(name = "is_delete", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    @Column(name = "is_deleted_for_sender", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeletedForSender;

    @Column(name = "is_deleted_for_receiver", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeletedForReceiver;

    @Column(name = "is_edited")
    @ColumnDefault("false")
    private Boolean isEdited = false;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;


    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
        if (this.isEdited == null) {
            this.isEdited = false;
        }
        if (this.isDeletedForSender == null) {
            this.isDeletedForSender = false;
        }
        if (this.isDeletedForReceiver == null) {
            this.isDeletedForReceiver = false;
        }
    }


}
