package com.learning.real_time_chat_application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private Boolean isActive;
    @Column(name = "is_delete", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }


}