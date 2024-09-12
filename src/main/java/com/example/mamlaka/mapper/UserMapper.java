package com.example.mamlaka.mapper;

import com.example.mamlaka.dto.AuthenticationResponseDto;
import com.example.mamlaka.dto.UserDto;
import com.example.mamlaka.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static User mapToUser(UserDto userDto, User user) {
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRoles(userDto.getRoles());
        return user;
    }

    public static AuthenticationResponseDto mapToAuthenticationUser(UserDetails userDetails, String jwtToken, AuthenticationResponseDto authenticationResponseDto) {
        authenticationResponseDto.setUsername(userDetails.getUsername());
        authenticationResponseDto.setAccessToken(jwtToken);

        // Set roles from UserDetails
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Extract the role name
                .collect(Collectors.toSet());
        authenticationResponseDto.setRoles(roles);

        // Extract and set the JWT expiration date
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("wvLx9oe4tUnf7OXrJ/8ZX5WkbhhLqEXR0NlhGJPs1+M=")
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        Date expiration = claims.getExpiration();
        authenticationResponseDto.setTokenExpiryDate(expiration);

        return authenticationResponseDto;
    }
}
