package com.dev.sphere.userService.entity;

import com.dev.sphere.userService.dto.PostDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name ;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String description;

    @OneToMany(mappedBy = "profile")
    private List<Post> posts;

}
