package com.example.demo.config;

import org.apereo.cas.client.session.SingleSignOutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    private SingleSignOutFilter singleSignOutFilter;
    private LogoutFilter logoutFilter;
    private CasAuthenticationProvider casAuthenticationProvider;
    private ServiceProperties serviceProperties;

    public WebSecurityConfig(SingleSignOutFilter singleSignOutFilter, LogoutFilter logoutFilter,
                             CasAuthenticationProvider casAuthenticationProvider,
                             ServiceProperties serviceProperties) {
        this.logoutFilter = logoutFilter;
        this.singleSignOutFilter = singleSignOutFilter;
        this.serviceProperties = serviceProperties;
        this.casAuthenticationProvider = casAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/secured", "/login").authenticated()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
            .addFilterBefore(logoutFilter, LogoutFilter.class)
            .csrf().ignoringRequestMatchers("/exit/cas")
            .and()
            .logout()
                .logoutUrl("/auth/logout")  // 로그아웃 URL 설정
                .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 후 리디렉션할 URL
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();  // 로그아웃 URL에 대한 접근 허용
        return http.build();
    }
    
    
	/*
	 * @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws
	 * Exception { http .authorizeRequests()
	 * .requestMatchers("/secured").authenticated() // 보호된 페이지
	 * .requestMatchers("/login", "/templates").permitAll() // 직접 만든 로그인 페이지 및 공개
	 * 페이지 .and() .exceptionHandling()
	 * .authenticationEntryPoint(authenticationEntryPoint()) // CAS 엔트리포인트 .and()
	 * .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
	 * .addFilterBefore(logoutFilter, LogoutFilter.class)
	 * .csrf().ignoringRequestMatchers("/exit/cas") // CSRF 예외 설정 .and()
	 * .formLogin() // Spring Security 폼 로그인 추가 .loginPage("/login") // 직접 만든 로그인
	 * 페이지 설정 .permitAll() .and() .logout() .logoutUrl("/auth/logout") // 로그아웃 URL
	 * 설정 .logoutSuccessUrl("/login?logout") // 로그아웃 성공 후 리디렉션할 URL
	 * .invalidateHttpSession(true) .deleteCookies("JSESSIONID") .permitAll(); //
	 * 로그아웃 URL에 대한 접근 허용 return http.build(); }
	 */


    // AuthenticationManager 빈을 명시적으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl("https://localhost:8443/cas/login");
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }
}
