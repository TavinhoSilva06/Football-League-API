package com.example.Football_League_API.security;

import com.example.Football_League_API.entity.Usuario;
import com.example.Football_League_API.enu.Role;
import com.example.Football_League_API.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = cabecalho.substring(7);
            if (jwtService.isValido(token)) {
                Optional<Usuario> usuario = usuarioRepository.findByEmail(jwtService.obterEmail(token));
                usuario.ifPresent(u -> {
                    Role role = u.getRole() == null ? Role.USER : u.getRole();
                    UsernamePasswordAuthenticationToken autenticacao =
                            new UsernamePasswordAuthenticationToken(u.getEmail(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
                    autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                });
            }
        }
        filterChain.doFilter(request, response);
    }
}
