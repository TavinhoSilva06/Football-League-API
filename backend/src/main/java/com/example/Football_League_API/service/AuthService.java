package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.LoginRequestDto;
import com.example.Football_League_API.dto.request.RegistroRequestDto;
import com.example.Football_League_API.dto.response.AuthResponseDto;
import com.example.Football_League_API.dto.response.UsuarioResponseDto;
import com.example.Football_League_API.entity.Usuario;
import com.example.Football_League_API.enu.Role;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.UsuarioMapper;
import com.example.Football_League_API.repository.UsuarioRepository;
import com.example.Football_League_API.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper mapper;

    public AuthResponseDto registrar(RegistroRequestDto dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("E-mail já cadastrado");
        }
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .role(Role.USER)
                .pontos(0)
                .build());
        return montarResposta(usuario);
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return montarResposta(usuario);
    }

    public UsuarioResponseDto me() {
        return mapper.toResponseDto(usuarioAutenticado());
    }

    public Usuario usuarioAutenticado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || autenticacao.getName() == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }
        return usuarioRepository.findByEmail(autenticacao.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + autenticacao.getName()));
    }

    private AuthResponseDto montarResposta(Usuario usuario) {
        return AuthResponseDto.builder()
                .token(jwtService.gerarToken(usuario))
                .tipo("Bearer")
                .usuario(mapper.toResponseDto(usuario))
                .build();
    }
}
