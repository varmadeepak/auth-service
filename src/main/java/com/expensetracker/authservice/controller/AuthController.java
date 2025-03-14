package com.expensetracker.authservice.controller;

import com.expensetracker.authservice.entity.RefreshToken;
import com.expensetracker.authservice.model.UserInfoDTO;
import com.expensetracker.authservice.response.JwtResponseDTO;
import com.expensetracker.authservice.service.JwtService;
import com.expensetracker.authservice.service.RefreshTokenService;
import com.expensetracker.authservice.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class AuthController {
    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  RefreshTokenService refreshTokenService;
    @Autowired
    private  UserDetailsServiceImpl userDetailsService;

    @PostMapping("/auth/v1/signup")
    public ResponseEntity<?> signup(@RequestBody UserInfoDTO userInfoDTO){
        try{
            String userId = userDetailsService.signUpUser(userInfoDTO);
            if(Objects.isNull(userId)) {
                return new ResponseEntity<>("User Already Exists", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDTO.getUsername());
            Map<String,Object> map = new HashMap<>();
            String jwtToken = jwtService.createToken(map,userInfoDTO.getUsername());
            JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .userId(userId)
                    .build();
            return new ResponseEntity<>(jwtResponseDTO,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity<String> ping(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Token : " + authentication);
        System.out.println("userName : " + authentication.getName());
        if(authentication != null && authentication.isAuthenticated()){
            String userId = userDetailsService.getUserByUserName(authentication.getName());
            System.out.println("UserID : " + userId);
            if(Objects.nonNull(userId)){
                return ResponseEntity.ok(userId);
            }
        }
        return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> printHello(){
        System.out.println("Hello");
        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }
}
