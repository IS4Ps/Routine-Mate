package com.hansung.adhd.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component // 스프링 빈으로 등록해서 어디서든 주입받아 쓸 수 있게!
public class JwtProvider {

    private final Key key;
    private final long accessExpiration;
    private final long refreshExpiration;

    // 생성자 주입: 스프링이 뜰 때 JwtProperties를 딱 가져와서 세팅해줌
    public JwtProvider(JwtProperties jwtProperties) {
        // 1. String으로 된 비밀키를 바이트 배열로 바꾸고
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        // 2. 강력한 HMAC SHA 암호화 알고리즘에 맞는 진짜 '키(Key)' 객체로 변환!
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.accessExpiration = jwtProperties.accessExpiration();
        this.refreshExpiration = jwtProperties.refreshExpiration();
    }

    // ... 이제 여기에 토큰 생성/검증 메서드를 만들 거네!
    /**
     * Access Token 생성기
     * @param userId 유저의 PK (부모 테이블 id 거나 아이 테이블 id)
     * @param role 권한 (ROLE_PARENT or ROLE_CHILD)
     */
    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessExpiration); // 지금 시간 + 1시간

        return Jwts.builder()
                .setSubject(userId.toString()) // 토큰의 주인 (보통 ID를 넣음)
                .claim("role", role)           // 추가 정보 (권한)
                .setIssuedAt(now)              // 발급 시간
                .setExpiration(validity)       // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 아까 만든 비밀키로 도장 쾅!
                .compact();                    // 얍! 문자열로 압축!
    }

    /**
     * 토큰 유효성 검사기
     */
    public boolean validateToken(String token) {
        try {
            // 우리가 가진 비밀키로 토큰의 서명을 뜯어본다. 에러가 안 나면 통과!
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 유저 정보를 뽑아내서 Spring Security용 인증 객체로 만드는 메서드
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰 배를 갈라서 내용물(Claims)을 꺼낸다
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 우리 서버의 비밀키로 열어야 열림!
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 2. 내용물 중에서 "role" (권한) 정보를 빼낸다. (예: "ROLE_CHILD" 또는 "ROLE_PARENT")
        // 만약 권한 정보가 없으면, 허가받지 않은 이상한 토큰이므로 에러를 던진다.
        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 3. 스프링 시큐리티가 이해할 수 있는 권한 객체(GrantedAuthority) 리스트로 변환!
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 4. 시큐리티의 기본 User 객체를 만든다.
        // (주의: 우리가 만든 엔티티가 아니라 org.springframework.security.core.userdetails.User 임!)
        // 토큰의 주체(Subject)에 넣어둔 유저 ID를 아이디 자리에 넣고, 비밀번호는 없으니 ""(빈 문자열) 처리.
        User principal = new User(claims.getSubject(), "", authorities);

        // 5. 최종적으로 SecurityContext에 들어갈 '인증된 뱃지(Authentication)'를 발급!
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    /**
     * 리프레시 토큰 생성기 (간단하고 강력한 UUID 사용!)
     */
    public String createRefreshToken() {
        return java.util.UUID.randomUUID().toString();
    }
}