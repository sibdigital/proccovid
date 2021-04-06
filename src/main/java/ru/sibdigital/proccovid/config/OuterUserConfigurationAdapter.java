package ru.sibdigital.proccovid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sibdigital.proccovid.service.UserDetailsServiceImpl;

@EnableWebSecurity()
@Configuration
@ComponentScan("ru.sibdigital.proccovid.service")
@Order(2)
public class OuterUserConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    public OuterUserConfigurationAdapter() {
        super();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);//.passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/libs/**", "/css/**").permitAll() //, "/templates/outer/**"
                .antMatchers("/outer/ologin").permitAll()
                //.antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/favicon.ico","/logo.png").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/outer/ologin")
                .loginProcessingUrl("/outer/perform_login")
                .failureUrl("/outer/ologin?error=true")

                .and()
                .logout()
                .logoutUrl("/outer/logout")
                .deleteCookies("JSESSIONID")

                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");
    }

}
