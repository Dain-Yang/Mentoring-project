package com.example.mentoring.global.config;

import com.example.mentoring.member.entity.User.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  // Role Enum에서 권한 문자열을 가져와 상수로 정의
  private final String MENTOR_AUTHORITY = Role.MENTOR.getAuthority();
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 설정 (JWT를 사용하므로 비활성화)
        .csrf(AbstractHttpConfigurer::disable)

        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)

        // 세션 관리 (JWT를 사용하므로 Stateless로 설정)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 요청 권한 설정
        .authorizeHttpRequests(auth -> auth
            // 인증 없이 접근 가능한 경로 (로그인, 로그아웃, 회원가입)
            .requestMatchers(
                "/login",
                "/logout",
                "/signup"
            ).permitAll()
            // 모집 게시글 조회 - all
            .requestMatchers(HttpMethod.GET, "/post").authenticated()
            .requestMatchers(HttpMethod.GET, "/post/{postId}").authenticated()
            .requestMatchers(HttpMethod.GET, "/post/list/{userId}").authenticated()

            // 신청서 제출 (누구나)
            .requestMatchers(HttpMethod.POST, "/application/post/**").authenticated()

            // 특정 게시글의 신청서 목록 조회 (멘토 전용)
            .requestMatchers(HttpMethod.GET, "/application/post/**").hasAuthority(MENTOR_AUTHORITY)

            // 멘토 전용 api
            .requestMatchers("/post/**").hasAuthority(MENTOR_AUTHORITY)
            .requestMatchers("/mentor/**").hasAuthority(MENTOR_AUTHORITY)
            //post GET 요청을 먼저 처리해야 하고 아랫줄에서 더 구체적인 규칙을 적용

            .requestMatchers("/application/{applicationId}/**").hasAuthority(MENTOR_AUTHORITY)

            // 그 외 모든 요청은 로그인만 되어있으면 통과
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}