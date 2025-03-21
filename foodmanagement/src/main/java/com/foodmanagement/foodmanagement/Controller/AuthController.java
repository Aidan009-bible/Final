package com.foodmanagement.foodmanagement.Controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodmanagement.foodmanagement.Config.UserAuthenticationProvider;
import com.foodmanagement.foodmanagement.dto.CredentialsDto;
import com.foodmanagement.foodmanagement.dto.SignUpDto;
import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.entity.enums.Role;
import com.foodmanagement.foodmanagement.service.UsersService;
import com.foodmanagement.foodmanagement.exception.InvalidRoleException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsersService usersService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UsersDTO> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UsersDTO usersDTO = usersService.login(credentialsDto);
        usersDTO.setToken(userAuthenticationProvider.createToken(usersDTO));
        return ResponseEntity.ok(usersDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> register(@RequestBody @Valid SignUpDto user) {
        if (user.role() == Role.ADMIN) {
            throw new InvalidRoleException("Cannot directly register as ADMIN");
        }
        UsersDTO createdUser = usersService.register(user);
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UsersDTO> registerAdmin(@RequestBody @Valid SignUpDto user) {
        SignUpDto adminUser = new SignUpDto(
            user.name(),
            user.email(),
            user.password(),
            user.address(),
            Role.ADMIN
        );
        
        UsersDTO createdUser = usersService.register(adminUser);
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // You could add any server-side cleanup here if needed
        // For example: invalidating tokens, clearing sessions, etc.
        return ResponseEntity.ok().build();
    }
} 