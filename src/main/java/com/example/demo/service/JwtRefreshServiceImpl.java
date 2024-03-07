package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.OAuth2ResponseDto;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.repository.data.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRefreshServiceImpl implements JwtRefreshService{
    private final UserJwtTokenService userJwtTokenService;
    private final UserRepository userRepository;

    @Override
    public OAuth2ResponseDto refresh(Long sub){
        Optional<User> user = userRepository.findByUserIdWithUserAuthorities(sub);
        if (user.isPresent()){
            return userJwtTokenService.createJWT(user.get());
        }else{
            log.debug("존재하지 않는 유저 정보 > {}" , sub);
            throw new CustomException(StatusCode.AUTHENTICATION_FAILED);
        }
    }
}
