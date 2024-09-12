package com.example.mamlaka.service;

import com.example.mamlaka.dto.AuthenticationRequestDto;
import com.example.mamlaka.dto.AuthenticationResponseDto;
import com.example.mamlaka.dto.UserDto;

public interface IUserService {

    /**
     * Register a new user
     */
    void registerUser(UserDto userDto);

    AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) throws Exception;
}
