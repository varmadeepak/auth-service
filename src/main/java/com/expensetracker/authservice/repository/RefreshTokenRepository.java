package com.expensetracker.authservice.repository;

import com.expensetracker.authservice.entity.RefreshToken;
import com.expensetracker.authservice.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Integer> {

    Optional<RefreshToken> findByToken(String token_1);
}
