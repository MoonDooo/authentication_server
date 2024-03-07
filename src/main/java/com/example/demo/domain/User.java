package com.example.demo.domain;

import com.example.demo.dto.en.GENDER;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private GENDER gender;

    private LocalDate birthDate;

    private String email;
    @CreationTimestamp
    private LocalDateTime createAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "longtext")
    private Map<String, String> addInfo;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private OAuth2UserInfo oAuth2UserInfo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserAuthorities> userAuthorities;

    @Builder
    public User(String name, String phoneNumber, GENDER gender, LocalDate birthDate, String email, OAuth2UserInfo oAuth2UserInfo, List<UserAuthorities> userAuthorities) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.email = email;
        this.oAuth2UserInfo = oAuth2UserInfo;
        this.userAuthorities = userAuthorities;
    }
}
