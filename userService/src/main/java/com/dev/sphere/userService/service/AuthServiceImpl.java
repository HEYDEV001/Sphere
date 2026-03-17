package com.dev.sphere.userService.service;

import com.dev.sphere.userService.dto.LoginDto;
import com.dev.sphere.userService.dto.LoginResponseDto;
import com.dev.sphere.userService.dto.SignUpDto;
import com.dev.sphere.userService.dto.UserDto;
import com.dev.sphere.userService.entity.User;
import com.dev.sphere.userService.exception.BadRequestException;
import com.dev.sphere.userService.exception.ResourceNotFoundException;
import com.dev.sphere.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.dev.sphere.userService.utils.PasswordUtil.checkPassword;
import static com.dev.sphere.userService.utils.PasswordUtil.hashPassword;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Override
    public UserDto signUp(SignUpDto signUpDto) {
        log.info("Signing up a new user with the following details: {}", signUpDto);
        Optional<User> user = userRepository.findByEmail(signUpDto.getEmail());
        if (user.isPresent()) {
            throw new BadRequestException("User with email already exits " + signUpDto.getEmail());
        }
        User toBeCreatedUser = modelMapper.map(signUpDto, User.class);
        toBeCreatedUser.setPassword(hashPassword(signUpDto.getPassword()));
        log.info("User created {}", toBeCreatedUser);
        User savedUser = userRepository.save(toBeCreatedUser);
        log.info("User saved {}", savedUser);
        return modelMapper.map(savedUser, UserDto.class);

    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        log.info("logging in a user with the following details: {}", loginDto);
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + loginDto.getEmail() + " not found"));

        boolean isPasswordMatches = checkPassword(loginDto.getPassword(), user.getPassword());
        if (!isPasswordMatches) {
            throw new BadRequestException("Wrong password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        log.info("JWT generated access token {}", accessToken);
        return new LoginResponseDto(user.getId(), accessToken);


    }
}
