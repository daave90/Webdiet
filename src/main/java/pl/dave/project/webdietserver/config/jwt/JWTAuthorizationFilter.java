package pl.dave.project.webdietserver.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private UserService userService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(JWTConstants.HEADER_STRING);

        if (header == null || !header.startsWith(JWTConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(JWTConstants.HEADER_STRING);
        if (token != null) {
            //parse the token
            String username = JWT.require(Algorithm.HMAC512(JWTConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(JWTConstants.TOKEN_PREFIX, ""))
                    .getSubject();

            if (username != null) {
                User user = userService.findByUsername(username);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
                return new UsernamePasswordAuthenticationToken(username, null, Arrays.asList(authority));
            }
            return null;
        }
        return null;
    }
}
