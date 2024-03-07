package com.example.demo.repository.data;

import com.example.demo.domain.OAuth2UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2UserInfoRepository extends JpaRepository<OAuth2UserInfo, Long> {

    @Query("SELECT COUNT(o)>0 FROM OAuth2UserInfo o WHERE o.oauth2Id = :id AND o.registrationId = :registrationId")
    boolean existsRegistrationIdAndId(String registrationId, String id);


    @Query("SELECT o FROM OAuth2UserInfo o JOIN FETCH o.user u WHERE o.registrationId = :registrationId AND o.oauth2Id = :id")
    Optional<OAuth2UserInfo> findByRegistrationIdAndIdWithUser(String registrationId, String id);

}
