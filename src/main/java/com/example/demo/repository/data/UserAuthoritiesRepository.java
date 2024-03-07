package com.example.demo.repository.data;

import com.example.demo.domain.UserAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthoritiesRepository extends JpaRepository<UserAuthorities, Long> {
}
