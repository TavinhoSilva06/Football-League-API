package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.Patente;
import com.example.Football_League_API.enu.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {

    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Integer pontos;
    private Patente patente;
    private Patente proximaPatente;
    private Integer pontosParaProxima;
}
