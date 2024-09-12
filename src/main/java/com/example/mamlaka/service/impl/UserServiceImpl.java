package com.example.mamlaka.service.impl;

import com.example.mamlaka.dto.AuthenticationRequestDto;
import com.example.mamlaka.dto.AuthenticationResponseDto;
import com.example.mamlaka.dto.UserDto;
import com.example.mamlaka.entity.User;
import com.example.mamlaka.exception.UserAlreadyExistsException;
import com.example.mamlaka.mapper.UserMapper;
import com.example.mamlaka.repository.UserRepository;
import com.example.mamlaka.security.JwtUtil;
import com.example.mamlaka.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private MyUserDetailsServiceImpl userDetailsService;
    private JwtUtil jwtUtil;

    @Override
    public void registerUser(UserDto userDto) {
        // Encode the password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // Assign default roles if not provided
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            userDto.setRoles(Set.of("ADMIN")); // Default role
        }

        User user = UserMapper.mapToUser(userDto, new User());
        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
        if(optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already registered with given username "
                    +userDto.getUsername());
        }
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDto.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        AuthenticationResponseDto authenticationResponseDto = UserMapper.mapToAuthenticationUser(userDetails, jwt, new AuthenticationResponseDto());

        return authenticationResponseDto;
    }
}
