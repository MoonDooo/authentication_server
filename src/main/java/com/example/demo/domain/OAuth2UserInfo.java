package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="oauth2_user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationId;

    private String oauth2Id;

    @OneToOne
    @JoinColumn(columnDefinition = "user_id")
    private User user;

    @Builder
    public OAuth2UserInfo(String registrationId, String oauth2Id, User user) {
        this.registrationId = registrationId;
        this.oauth2Id = oauth2Id;
        this.user = user;
    }
}
