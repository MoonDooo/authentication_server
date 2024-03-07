package com.example.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class JwtTokenDetails {
    private Date exp;
    private String id;
    private Date iat;
    public JwtTokenDetails(Date expiration, String id, Date iat) {
        this.exp = expiration;
        this.id = id;
        this.iat = iat;
    }
}
