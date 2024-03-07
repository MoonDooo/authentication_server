package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserIdAndAuthorities {
    private String userId;
    private List<String> authorities;
}
