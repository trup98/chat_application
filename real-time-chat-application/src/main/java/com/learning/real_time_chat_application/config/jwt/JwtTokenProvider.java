package com.learning.real_time_chat_application.config.jwt;

import com.learning.real_time_chat_application.enums.CommonEnum;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.enums.JwtExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final JWSSigner jwsSigner;
    private final JWSVerifier jwsVerifier;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) throws JOSEException {
        this.jwsSigner = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
        this.jwsVerifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonEnum.AUTHORIZATION.getValue());
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //  this method will validate the token
    public Boolean validateToken(String token) {
        this.parseJwtAndExtractClaims(token);
        return this.isTokenExpired(token);
    }


    public Authentication getAuthentication(String token) {
        try {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(getUserRole(token)));
            UserDetails userDetails = new User(getUsername(token), getUsername(token), true, false, false, false, grantedAuthorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            throw new CustomException(ExceptionEnum.TOKEN_WITHOUT_ROLE.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public String getUsername(String token) {
        return this.parseJwtAndExtractClaims(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return (Long) this.parseJwtAndExtractClaims(token).getClaim(CommonEnum.USER_ID.getValue());
    }

    public String getUserRole(String token) {
        return (String) this.parseJwtAndExtractClaims(token).getClaim(CommonEnum.USER_ROLE.getValue());
    }

    private Boolean isTokenExpired(String token) {
        if (extractExpiration(token).before(new Date())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Date extractExpiration(String token) {
        return this.parseJwtAndExtractClaims(token).getExpirationTime();
    }


    //  in this method we break down JWT token and verify their signatures and getting the claims through claims we have header,payload,sign;
    private JWTClaimsSet parseJwtAndExtractClaims(String jwtToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            boolean isValid = signedJWT.verify(this.jwsVerifier);
            if (isValid) {
                return signedJWT.getJWTClaimsSet();
            }
            throw new CustomException(ExceptionEnum.INVALID_TOKEN.getValue(), HttpStatus.UNAUTHORIZED);
        } catch (JOSEException | ParseException e) {
            throw new CustomException(ExceptionEnum.INVALID_TOKEN.getValue(), HttpStatus.UNAUTHORIZED);
        }
    }

    private SignedJWT generateSignedJwt(String subject, Map<String, Object> customClaims) {
        try {
            // verify claims & parse to JWTClaimsSet
            var jwtClaimsSet = verifyClaims(customClaims);
            Date now = new Date();
            // validity set for 15 minutes as per standards
            Date validity = new Date(now.getTime() + 900000);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            // create payload for token
            JWTClaimsSet payload = new JWTClaimsSet.Builder(jwtClaimsSet)
                    .issuer("jwt-security")
                    .subject(subject)
                    .issueTime(now)
                    .expirationTime(validity)
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, payload);
            signedJWT.sign(this.jwsSigner);
            return signedJWT;
        } catch (JOSEException e) {
            log.error("Exception while creating token ::: ", e);
            throw new CustomException(JwtExceptionEnum.SOMETHING_WENT_WRONG.getValue(), HttpStatus.UNAUTHORIZED);
        }
    }

    public JWTClaimsSet verifyClaims(Map<String, Object> customClaims) {
        try {
            // check if custom claims are present with key and value
            Map<String, Object> actualClaims = Optional.ofNullable(customClaims)
                    .orElseGet(Collections::emptyMap)
                    .entrySet()
                    .stream()
                    .filter(foundEntry -> List.of(CommonEnum.USER_ID.getValue(), CommonEnum.USER_ROLE.getValue()).contains(foundEntry.getKey()))
                    .filter(foundEntry -> Optional.ofNullable(foundEntry.getValue()).isPresent() && Optional.ofNullable(foundEntry.getKey()).isPresent())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            JWTClaimsSet parse = JWTClaimsSet.parse(actualClaims);
            return parse;
        } catch (Exception e) {
            throw new CustomException(JwtExceptionEnum.INVALID_TOKEN.getValue(), HttpStatus.UNAUTHORIZED);
        }
    }

    public String createToken(String username, String userRole, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CommonEnum.USER_NAME.getValue(), username);
        claims.put(CommonEnum.USER_ROLE.getValue(), userRole);
        claims.put(CommonEnum.USER_ID.getValue(), userId);
        // new with nimbus jose jwt
        return this.generateSignedJwt(username, claims).serialize();
    }

    public String createNewTokenFromToken(String token) {
        var jwtClaimsSet = this.parseJwtAndExtractClaims(token);
        var claims = jwtClaimsSet.getClaims();
        return this.generateSignedJwt(jwtClaimsSet.getSubject(), claims).serialize();
    }
}
