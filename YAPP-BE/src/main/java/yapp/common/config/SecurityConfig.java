package yapp.common.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.filter.TokenAuthenticationFilter;
import yapp.common.oauth.handler.OAuth2AuthenticationFailureHandler;
import yapp.common.oauth.handler.OAuth2AuthenticationSuccessHandler;
import yapp.common.oauth.handler.TokenAccessDeniedHandler;
import yapp.common.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import yapp.common.oauth.service.CustomUserDetailsService;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.domain.member.repository.MemberRefreshTokenRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final CorsProperties corsProperties;
  private final RedisTemplate redisTemplate;
  private final AppProperties appProperties;
  private final AuthTokenProvider tokenProvider;
  private final CustomUserDetailsService userDetailsService;
  private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .cors()
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .csrf()
      .disable()
      .formLogin()
      .disable()
      .httpBasic()
      .disable()
      .authorizeRequests()
      .requestMatchers(CorsUtils::isPreFlightRequest)
      .permitAll()
      .antMatchers(
        "/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html",
        "/**/*.css", "/**/*.js"
      )
      .permitAll()
      .antMatchers(
        "/auth/token", "/oauth2/**", "/auth/**", "/health", "/oauth/redirect", "/auth/login")
      .permitAll() // Security 허용 Url
      .antMatchers("/register")
      .permitAll()
      .antMatchers("/register/**")
      .permitAll()
      .antMatchers("/app/members/**")
      .permitAll()
      .antMatchers("/app/townMap/**")
      .permitAll()
      .antMatchers("/app/town/filter")
      .permitAll()
      .antMatchers("/app/town/search/**")
      .permitAll()
      .antMatchers("/app/town/introduce")
      .permitAll()
      .antMatchers("/refresh")
      .permitAll()
      .antMatchers("/member/**")
      .hasAnyAuthority(RoleType.USER.getCode())
      .antMatchers("/api/**/admin/**")
      .hasAnyAuthority(RoleType.ADMIN.getCode())
      .antMatchers("/swagger-resources/**")
      .permitAll()
      .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-town-scoop.html")
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .exceptionHandling()
//      .authenticationEntryPoint(new RestAuthenticationEntryPoint())
      .accessDeniedHandler(tokenAccessDeniedHandler);
//      .and()
//      .oauth2Login()
//      .authorizationEndpoint()
//      .baseUri("/oauth2/authorization")
//      .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
//      .and()
//      .redirectionEndpoint()
//      .baseUri("/oauth2/callback/*")
//      .and()
//      .userInfoEndpoint()
//      .userService(oAuth2UserService)
//      .and()
//      .successHandler(oAuth2AuthenticationSuccessHandler())
//      .failureHandler(oAuth2AuthenticationFailureHandler());

    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(tokenProvider, redisTemplate);
  }

  @Bean
  public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
    return new OAuth2AuthorizationRequestBasedOnCookieRepository();
  }

  @Bean
  public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
    return new OAuth2AuthenticationSuccessHandler(
      tokenProvider,
      appProperties,
      memberRefreshTokenRepository,
      oAuth2AuthorizationRequestBasedOnCookieRepository()
    );
  }

  @Bean
  public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
    return new OAuth2AuthenticationFailureHandler(
      oAuth2AuthorizationRequestBasedOnCookieRepository());
  }

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
    corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
    corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
    corsConfig.setAllowCredentials(true);
    corsConfig.setMaxAge(corsConfig.getMaxAge());

    corsConfigSource.registerCorsConfiguration("/**", corsConfig);
    return corsConfigSource;
  }
}
