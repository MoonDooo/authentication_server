package com.example.demo.dto.converter;

import com.example.demo.domain.User;
import com.example.demo.dto.NaverOAuth2Dto;
import com.example.demo.dto.en.GENDER;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NaverUserConverter implements Converter<NaverOAuth2Dto, User> {
    @Override
    public User convert(NaverOAuth2Dto dto) {
        Assert.notNull(dto.getAttribute("name"), "cannot have user's name");
        Assert.notNull(dto.getAttribute("mobile"), "cannot have users' phoneNumber");

        return User.builder()
                .name(dto.getAttribute("name"))
                .phoneNumber(dto.getAttribute("mobile"))
                .email(dto.getAttribute("email"))
                .gender(getGender(dto.getAttribute("gender")))
                .birthDate(getBirthDate(dto.getAttribute("birthyear"), dto.getAttribute("birthday"))).build();
    }

    private static LocalDate getBirthDate(String birthYear, String birthDay) {
        if (birthYear == null || birthDay == null)return null;
        return LocalDate.parse(birthYear + "-" + birthDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private GENDER getGender(String gender) {
        switch (gender){
            case "M"-> {
                return GENDER.MALE;
            }
            case "F"-> {
                return GENDER.FEMALE;
            }
            case "U"-> {
                return GENDER.UNKNOWN;
            }
        }
        return GENDER.UNKNOWN;
    }
}
