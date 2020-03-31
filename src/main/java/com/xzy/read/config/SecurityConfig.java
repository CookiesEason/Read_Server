package com.xzy.read.config;

import com.xzy.read.filter.JwtAuthorizationTokenFilter;
import com.xzy.read.handler.AuthenticationFailureHandler;
import com.xzy.read.handler.AuthenticationSuccessHandler;
import com.xzy.read.handler.LogoutHandle;
import com.xzy.read.service.impl.UserServiceImpl;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author XieZhongYi
 * 2020/03/23 15:03
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/api/notebooks").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/api/articles").hasAnyAuthority("USER","ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/follow").hasAnyAuthority("USER","ADMIN")
                .anyRequest().permitAll()
                .and()
                    .formLogin()
                    .loginPage("/api/user/relogin")
                    .loginProcessingUrl("/api/user/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
                    .permitAll()
                .and()
                    .logout()
                    .logoutUrl("/api/user/logout")
                    .logoutSuccessHandler(logoutHandle())
                    .permitAll()
                .and()
                    .addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new AuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new AuthenticationFailureHandler();
    }

    @Bean
    public JwtAuthorizationTokenFilter authenticationTokenFilter(){
        return new JwtAuthorizationTokenFilter ();
    }

    @Bean
    public LogoutHandle logoutHandle(){
        return new LogoutHandle();
    }
}
