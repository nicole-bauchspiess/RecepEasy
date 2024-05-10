package com.recepEasy.api.security;

import com.recepEasy.api.usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperToken(request);

        if(tokenJWT!= null) {
            var login = tokenService.getSubject(tokenJWT);
            var usuario = service.findByLogin(login);
            var authenticathion = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticathion);
        }
        filterChain.doFilter(request, response);

    }

    public String recuperToken(HttpServletRequest request) {
        var authorizathionHeader = request.getHeader("Authorization"); //nome = authorizathion
        if(authorizathionHeader!=null){
            return authorizathionHeader.replace("Bearer ", "");
        }
        return null;
    }

}
