package com.foodmanagement.foodmanagement.service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodmanagement.foodmanagement.dto.CredentialsDto;
import com.foodmanagement.foodmanagement.dto.SignUpDto;
import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.entity.Users;
import com.foodmanagement.foodmanagement.exception.AppException;
import com.foodmanagement.foodmanagement.mappers.UserMapper;
import com.foodmanagement.foodmanagement.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsersService {
    // @Autowired
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UsersDTO login(CredentialsDto credentialsDto) {
        Users user = usersRepository.findByemail(credentialsDto.email())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUsersDTO(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UsersDTO register(SignUpDto userDto) {
        Optional<Users> optionalUser = usersRepository.findByemail(userDto.email());

        if (optionalUser.isPresent()) {
            throw new AppException("email already exists", HttpStatus.BAD_REQUEST);
        }

        Users user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        Users savedUser = usersRepository.save(user);

        return userMapper.toUsersDTO(savedUser);
    }

    public UsersDTO findByemail(String email) {
        Users user = usersRepository.findByemail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUsersDTO(user);
    }

    // Add your service methods here

    public long getUsersCount() {
        return usersRepository.count();
    }

    public Optional<Users> getUserById(Integer id) {
        return usersRepository.findById(id);
    }

    public Object getUsersCountByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsersCountByDate'");
    }
}
