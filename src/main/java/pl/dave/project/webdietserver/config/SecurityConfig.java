package pl.dave.project.webdietserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.dave.project.webdietserver.config.jwt.JWTAuthenticationFilter;
import pl.dave.project.webdietserver.config.jwt.JWTAuthorizationFilter;
import pl.dave.project.webdietserver.service.UserService;

import java.security.SecureRandom;

/*
 * 6. testy
 * */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users").hasAnyAuthority("ADMIN")
                .antMatchers("/users").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/users/*").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/products").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/products/*").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/recipes").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/recipes/*").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/shopping-lists").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/shopping-lists/*").hasAnyAuthority("ADMIN", "USER")
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userService))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
