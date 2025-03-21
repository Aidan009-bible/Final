package com.foodmanagement.foodmanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.foodmanagement.foodmanagement.dto.SignUpDto;
import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.entity.Users;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    @Mapping(target = "profileImage", source = "profileImage")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    UsersDTO toUsersDTO(Users users);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lockTime", ignore = true)
    @Mapping(target = "failedAttempt", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "isEnabled", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    Users signUpToUser(SignUpDto signUpDto);

    default String map(byte[] profileImage) {
        return profileImage != null ? Base64.getEncoder().encodeToString(profileImage) : null;
    }
}
