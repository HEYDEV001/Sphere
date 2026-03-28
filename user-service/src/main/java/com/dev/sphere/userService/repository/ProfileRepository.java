package com.dev.sphere.userService.repository;

import com.dev.sphere.userService.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUserId(Long userId);

    List<Profile> findByNameContainingIgnoreCase(String name);
}
