package com.expensetracker.authservice.controller;

import com.expensetracker.authservice.entity.RefreshToken;
import com.expensetracker.authservice.request.AuthRequestDTO;
import com.expensetracker.authservice.request.RefreshTokenRequest;
import com.expensetracker.authservice.response.JwtResponseDTO;
import com.expensetracker.authservice.service.JwtService;
import com.expensetracker.authservice.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/auth/v1/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(),authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            Map<String,Object> map = new HashMap<>();
            JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                    .accessToken(jwtService.createToken(map,authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .build();
            return new ResponseEntity<>(jwtResponseDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Invalid Username or Password",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/auth/v1/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request){
        Map<String,Object> map = new HashMap<>();
        Optional<JwtResponseDTO> jwtResponseDTO = refreshTokenService.findByToken(request.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(UserInfo -> {
                    String accessToken = jwtService.createToken(map,UserInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(request.getToken())
                            .build();
                });
        if(jwtResponseDTO.isPresent())
            return new ResponseEntity<>(jwtResponseDTO.get(),HttpStatus.OK);
        return new ResponseEntity<>("Refresh Token Not Found",HttpStatus.NOT_FOUND);
    }
}
