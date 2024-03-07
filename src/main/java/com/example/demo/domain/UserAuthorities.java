package com.example.demo.domain;

import com.example.demo.dto.en.ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "user_authorities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthorities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    User user;

    @Builder
    public UserAuthorities(ROLE role, User user) {
        this.role = role;
        this.user = user;
    }
}
