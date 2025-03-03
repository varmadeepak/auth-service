package com.expensetracker.authservice.service;

import com.expensetracker.authservice.entity.UserInfo;
import com.expensetracker.authservice.eventProducer.UserInfoEvent;
import com.expensetracker.authservice.eventProducer.UserInfoProducer;
import com.expensetracker.authservice.model.UserInfoDTO;
import com.expensetracker.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Data
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserInfoProducer userInfoProducer;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo =  userRepository.findByUsername(username);
        if(userInfo == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUserDetails(userInfo);
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDTO userInfoDTO){
        return userRepository.findByUsername(userInfoDTO.getUsername());
    }

    public String signUpUser(UserInfoDTO userInfoDTO){
        userInfoDTO.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExists(userInfoDTO))){
            return null;
        }
        String userId = UUID.randomUUID().toString();
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .username(userInfoDTO.getUsername())
                .password(userInfoDTO.getPassword())
                .roles(new HashSet<>())
                .build();
        userRepository.save(userInfo);
        userInfoProducer.sendEventToKafka(buildUserInfoEvent(userInfoDTO,userId));
        return userId;
    }

    public String getUserByUserName(String userName){
        return Optional.of(userRepository.findByUsername(userName)).map(UserInfo::getUserId).orElse(null);
    }
    private UserInfoEvent buildUserInfoEvent(UserInfoDTO userInfoDTO,String userId){
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDTO.getFirstName())
                .lastName(userInfoDTO.getLastName())
                .email(userInfoDTO.getEmail())
                .phoneNumber(userInfoDTO.getPhoneNumber())
                .build();
    }
}
