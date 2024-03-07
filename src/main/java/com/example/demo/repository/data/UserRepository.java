package com.example.demo.repository.data;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u JOIN FETCH u.oAuth2UserInfo oui JOIN FETCH u.userAuthorities ua WHERE oui.registrationId = :registrationId AND oui.oauth2Id = :oauth2Id")
    Optional<User> findByRegistrationIdAndOauth2IdWithOAuth2UserAndAuthorities(String registrationId, String oauth2Id);

    @Modifying
    @Query("DELETE FROM User u WHERE u.oAuth2UserInfo.oauth2Id = :id AND u.oAuth2UserInfo.registrationId = :clientRegistrationId")
    void deleteByRegistrationIdAndOAuth2Id(String clientRegistrationId, String id);

    @Query("SELECT u FROM User u JOIN FETCH u.userAuthorities ua WHERE u.id = :userId")
    Optional<User> findByUserIdWithUserAuthorities(Long userId);
}
