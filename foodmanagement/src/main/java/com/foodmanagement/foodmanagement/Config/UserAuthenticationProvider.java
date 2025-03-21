package com.foodmanagement.foodmanagement.Config;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.foodmanagement.foodmanagement.dto.UsersDTO;
import com.foodmanagement.foodmanagement.exception.AppException;
import com.foodmanagement.foodmanagement.service.UsersService;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UsersService userService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UsersDTO user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        return JWT.create()
                .withIssuer("restaurant_grp1")
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("restaurant_grp1")
                .build();

        DecodedJWT decoded = verifier.verify(token);
        
        // Just store the email as the principal instead of the entire UsersDTO
        String email = decoded.getClaim("email").asString();

        return new UsernamePasswordAuthenticationToken(email, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + decoded.getClaim("role").asString())));
    }

    public Authentication validateTokenStrongly(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();


        DecodedJWT decoded = verifier.verify(token);

        String email = decoded.getClaim("email").asString();
        if (email == null) {
            throw new AppException("Invalid token: missing email claim", null);
        }

        UsersDTO user = userService.findByemail(email);

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

}
