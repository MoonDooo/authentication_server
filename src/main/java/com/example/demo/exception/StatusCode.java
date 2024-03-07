package com.example.demo.exception;

import lombok.Getter;
import org.hibernate.Internal;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(HttpStatus.OK, "0000","SUCCESS"),
    AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "4444", "잘못된 접근입니다."),
    Internal_Server_Error(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "죄송합니다. 요청을 처리하는 과정에서 예상치 못한 서버 오류가 발생했습니다. 문제를 빠르게 해결하기 위해 노력하고 있습니다. 문제가 지속될 경우, 이메일로 문의해 주세요."),
    UNSUPPORTED_OAUTH2_REQUIREMENT(HttpStatus.NOT_IMPLEMENTED, "5002", "로그인 시도 중 문제가 발생하였습니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "4003", "유효하지 않는 접근입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "4002", "로그인 유지 시간 만료되었습니다."),
    EXPIRED_LOGIN(HttpStatus.UNAUTHORIZED, "4001", "로그인이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    StatusCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
