package com.example.Football_League_API.config;

import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Participacao;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.enu.FormatoCampeonato;
import com.example.Football_League_API.enu.PosicaoJogador;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.enu.StatusTemporada;
import com.example.Football_League_API.enu.TipoCampeonato;
import com.example.Football_League_API.repository.CampeonatoRepository;
import com.example.Football_League_API.repository.JogadorRepository;
import com.example.Football_League_API.repository.ParticipacaoRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CampeonatoRepository campeonatoRepository;
    private final TemporadaRepository temporadaRepository;
    private final TimeRepository timeRepository;
    private final JogadorRepository jogadorRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final PartidaRepository partidaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (campeonatoRepository.count() > 0) {
            return;
        }

        // Criar campeonatos
        Campeonato premierLeague = Campeonato.builder()
                .nome("Premier League")
                .pais("Inglaterra")
                .tipo(TipoCampeonato.LIGA_NACIONAL)
                .formato(FormatoCampeonato.PONTOS_CORRIDOS)
                .descricao("Campeonato profissional de futebol inglês")
                .build();
        campeonatoRepository.save(premierLeague);

        Campeonato serieA = Campeonato.builder()
                .nome("Série A")
                .pais("Brasil")
                .tipo(TipoCampeonato.LIGA_NACIONAL)
                .formato(FormatoCampeonato.PONTOS_CORRIDOS)
                .descricao("Campeonato profissional de futebol brasileiro")
                .build();
        campeonatoRepository.save(serieA);

        Campeonato bundesliga = Campeonato.builder()
                .nome("Bundesliga")
                .pais("Alemanha")
                .tipo(TipoCampeonato.LIGA_NACIONAL)
                .formato(FormatoCampeonato.PONTOS_CORRIDOS)
                .descricao("Campeonato profissional de futebol alemão")
                .build();
        campeonatoRepository.save(bundesliga);

        // Criar temporadas com número de rodadas
        Temporada premierLeague2024 = Temporada.builder()
                .campeonato(premierLeague)
                .nome("2024/25")
                .dataInicio(LocalDate.of(2024, 8, 16))
                .dataFim(LocalDate.of(2025, 5, 25))
                .numRodadas(38) // Premier League tem 38 rodadas
                .status(StatusTemporada.EM_ANDAMENTO)
                .build();
        temporadaRepository.save(premierLeague2024);

        Temporada serieA2024 = Temporada.builder()
                .campeonato(serieA)
                .nome("2024")
                .dataInicio(LocalDate.of(2024, 4, 13))
                .dataFim(LocalDate.of(2024, 11, 8))
                .numRodadas(38) // Série A tem 38 rodadas
                .status(StatusTemporada.EM_ANDAMENTO)
                .build();
        temporadaRepository.save(serieA2024);

        Temporada bundesliga2024 = Temporada.builder()
                .campeonato(bundesliga)
                .nome("2024/25")
                .dataInicio(LocalDate.of(2024, 8, 16))
                .dataFim(LocalDate.of(2025, 5, 24))
                .numRodadas(34) // Bundesliga tem 34 rodadas
                .status(StatusTemporada.EM_ANDAMENTO)
                .build();
        temporadaRepository.save(bundesliga2024);

        // Criar times
        Time manchesterCity = Time.builder()
                .nome("Manchester City")
                .sigla("MCI")
                .pais("Inglaterra")
                .cidade("Manchester")
                .estadio("Etihad Stadium")
                .build();
        timeRepository.save(manchesterCity);

        Time liverpool = Time.builder()
                .nome("Liverpool")
                .sigla("LIV")
                .pais("Inglaterra")
                .cidade("Liverpool")
                .estadio("Anfield")
                .build();
        timeRepository.save(liverpool);

        Time arsenal = Time.builder()
                .nome("Arsenal")
                .sigla("ARS")
                .pais("Inglaterra")
                .cidade("Londres")
                .estadio("Emirates Stadium")
                .build();
        timeRepository.save(arsenal);

        Time chelsea = Time.builder()
                .nome("Chelsea")
                .sigla("CHE")
                .pais("Inglaterra")
                .cidade("Londres")
                .estadio("Stamford Bridge")
                .build();
        timeRepository.save(chelsea);

        Time flamengo = Time.builder()
                .nome("Flamengo")
                .sigla("FLA")
                .pais("Brasil")
                .cidade("Rio de Janeiro")
                .estadio("Maracanã")
                .build();
        timeRepository.save(flamengo);

        Time saopaulo = Time.builder()
                .nome("São Paulo FC")
                .sigla("SPA")
                .pais("Brasil")
                .cidade("São Paulo")
                .estadio("Morumbi")
                .build();
        timeRepository.save(saopaulo);

        Time bayernMunique = Time.builder()
                .nome("Bayern de Munique")
                .sigla("BAY")
                .pais("Alemanha")
                .cidade("Munique")
                .estadio("Allianz Arena")
                .build();
        timeRepository.save(bayernMunique);

        Time borussiaDortmund = Time.builder()
                .nome("Borussia Dortmund")
                .sigla("BVB")
                .pais("Alemanha")
                .cidade("Dortmund")
                .estadio("Signal Iduna Park")
                .build();
        timeRepository.save(borussiaDortmund);

        // Criar jogadores
        Jogador halland = Jogador.builder()
                .nome("Erling Haaland")
                .time(manchesterCity)
                .nacionalidade("Noruega")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(9)
                .dataNascimento(LocalDate.of(2000, 7, 21))
                .build();
        jogadorRepository.save(halland);

        Jogador mahrez = Jogador.builder()
                .nome("Riyad Mahrez")
                .time(manchesterCity)
                .nacionalidade("Argélia")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(26)
                .dataNascimento(LocalDate.of(1991, 2, 21))
                .build();
        jogadorRepository.save(mahrez);

        Jogador ederson = Jogador.builder()
                .nome("Ederson Moraes")
                .time(manchesterCity)
                .nacionalidade("Brasil")
                .posicao(PosicaoJogador.GOLEIRO)
                .numeroCamisa(1)
                .dataNascimento(LocalDate.of(1995, 8, 17))
                .build();
        jogadorRepository.save(ederson);

        Jogador salah = Jogador.builder()
                .nome("Mohamed Salah")
                .time(liverpool)
                .nacionalidade("Egito")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(11)
                .dataNascimento(LocalDate.of(1992, 6, 15))
                .build();
        jogadorRepository.save(salah);

        Jogador vvd = Jogador.builder()
                .nome("Virgil van Dijk")
                .time(liverpool)
                .nacionalidade("Holanda")
                .posicao(PosicaoJogador.DEFESA)
                .numeroCamisa(4)
                .dataNascimento(LocalDate.of(1991, 7, 8))
                .build();
        jogadorRepository.save(vvd);

        Jogador alisson = Jogador.builder()
                .nome("Alisson Ramses")
                .time(liverpool)
                .nacionalidade("Brasil")
                .posicao(PosicaoJogador.GOLEIRO)
                .numeroCamisa(1)
                .dataNascimento(LocalDate.of(1992, 10, 2))
                .build();
        jogadorRepository.save(alisson);

        Jogador saka = Jogador.builder()
                .nome("Bukayo Saka")
                .time(arsenal)
                .nacionalidade("Inglaterra")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(7)
                .dataNascimento(LocalDate.of(2001, 9, 5))
                .build();
        jogadorRepository.save(saka);

        Jogador martinelli = Jogador.builder()
                .nome("Gabriel Martinelli")
                .time(arsenal)
                .nacionalidade("Brasil")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(11)
                .dataNascimento(LocalDate.of(2001, 6, 18))
                .build();
        jogadorRepository.save(martinelli);

        Jogador ramsdale = Jogador.builder()
                .nome("Aaron Ramsdale")
                .time(arsenal)
                .nacionalidade("Inglaterra")
                .posicao(PosicaoJogador.GOLEIRO)
                .numeroCamisa(1)
                .dataNascimento(LocalDate.of(1998, 5, 14))
                .build();
        jogadorRepository.save(ramsdale);

        Jogador neymar = Jogador.builder()
                .nome("Neymar Jr")
                .time(flamengo)
                .nacionalidade("Brasil")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(10)
                .dataNascimento(LocalDate.of(1992, 2, 5))
                .build();
        jogadorRepository.save(neymar);

        Jogador vinicius = Jogador.builder()
                .nome("Vinicius Souza")
                .time(saopaulo)
                .nacionalidade("Brasil")
                .posicao(PosicaoJogador.MEIO_CAMPO)
                .numeroCamisa(27)
                .dataNascimento(LocalDate.of(2000, 7, 4))
                .build();
        jogadorRepository.save(vinicius);

        Jogador lewandowski = Jogador.builder()
                .nome("Robert Lewandowski")
                .time(bayernMunique)
                .nacionalidade("Polônia")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(9)
                .dataNascimento(LocalDate.of(1988, 8, 21))
                .build();
        jogadorRepository.save(lewandowski);

        Jogador muller = Jogador.builder()
                .nome("Thomas Müller")
                .time(bayernMunique)
                .nacionalidade("Alemanha")
                .posicao(PosicaoJogador.MEIO_CAMPO)
                .numeroCamisa(25)
                .dataNascimento(LocalDate.of(1988, 9, 13))
                .build();
        jogadorRepository.save(muller);

        Jogador reus = Jogador.builder()
                .nome("Marco Reus")
                .time(borussiaDortmund)
                .nacionalidade("Alemanha")
                .posicao(PosicaoJogador.ATACANTE)
                .numeroCamisa(11)
                .dataNascimento(LocalDate.of(1990, 5, 31))
                .build();
        jogadorRepository.save(reus);

        // Criar participações (times em temporadas)
        participacaoRepository.save(Participacao.builder().temporada(premierLeague2024).time(manchesterCity).build());
        participacaoRepository.save(Participacao.builder().temporada(premierLeague2024).time(liverpool).build());
        participacaoRepository.save(Participacao.builder().temporada(premierLeague2024).time(arsenal).build());
        participacaoRepository.save(Participacao.builder().temporada(premierLeague2024).time(chelsea).build());

        participacaoRepository.save(Participacao.builder().temporada(serieA2024).time(flamengo).build());
        participacaoRepository.save(Participacao.builder().temporada(serieA2024).time(saopaulo).build());

        participacaoRepository.save(Participacao.builder().temporada(bundesliga2024).time(bayernMunique).build());
        participacaoRepository.save(Participacao.builder().temporada(bundesliga2024).time(borussiaDortmund).build());

        // Criar partidas (Premier League 2024/25 - Rodadas 1 a 5 para exemplo)
        // Rodada 1 - FINALIZADA
        partidaRepository.save(Partida.builder()
                .temporada(premierLeague2024)
                .rodada(1)
                .dataHora(LocalDateTime.of(2024, 8, 16, 20, 0))
                .timeMandante(manchesterCity)
                .timeVisitante(chelsea)
                .golsMandante(2)
                .golsVisitante(0)
                .status(StatusPartida.FINALIZADA)
                .local("Etihad Stadium")
                .build());

        partidaRepository.save(Partida.builder()
                .temporada(premierLeague2024)
                .rodada(1)
                .dataHora(LocalDateTime.of(2024, 8, 16, 15, 0))
                .timeMandante(liverpool)
                .timeVisitante(arsenal)
                .golsMandante(1)
                .golsVisitante(1)
                .status(StatusPartida.FINALIZADA)
                .local("Anfield")
                .build());

        // Rodada 2 - EM_ANDAMENTO
        partidaRepository.save(Partida.builder()
                .temporada(premierLeague2024)
                .rodada(2)
                .dataHora(LocalDateTime.of(2024, 8, 24, 20, 0))
                .timeMandante(arsenal)
                .timeVisitante(manchesterCity)
                .status(StatusPartida.EM_ANDAMENTO)
                .local("Emirates Stadium")
                .build());

        partidaRepository.save(Partida.builder()
                .temporada(premierLeague2024)
                .rodada(2)
                .dataHora(LocalDateTime.of(2024, 8, 24, 15, 0))
                .timeMandante(chelsea)
                .timeVisitante(liverpool)
                .status(StatusPartida.EM_ANDAMENTO)
                .local("Stamford Bridge")
                .build());

        // Rodada 3 a 38 - AGENDADAS (criando apenas rodada 3 como exemplo, resto será AGENDADA)
        partidaRepository.save(Partida.builder()
                .temporada(premierLeague2024)
                .rodada(3)
                .dataHora(LocalDateTime.of(2024, 8, 31, 20, 0))
                .timeMandante(manchesterCity)
                .timeVisitante(arsenal)
                .status(StatusPartida.AGENDADA)
                .local("Etihad Stadium")
                .build());

        // Criar partidas placeholder para rodadas 4-38 (para não poluir o código)
        for (int rodada = 4; rodada <= 38; rodada++) {
            int timeIdx = (rodada % 4);
            Time mandante = timeIdx == 0 ? manchesterCity : (timeIdx == 1 ? liverpool : (timeIdx == 2 ? arsenal : chelsea));
            Time visitante = timeIdx == 0 ? chelsea : (timeIdx == 1 ? manchesterCity : (timeIdx == 2 ? liverpool : arsenal));

            partidaRepository.save(Partida.builder()
                    .temporada(premierLeague2024)
                    .rodada(rodada)
                    .dataHora(LocalDateTime.of(2024, 8, 31 + (rodada * 7), 20, 0))
                    .timeMandante(mandante)
                    .timeVisitante(visitante)
                    .status(StatusPartida.AGENDADA)
                    .local("Estádio genérico")
                    .build());
        }

        // Criar partidas (Série A 2024 - Rodadas 1 a 38)
        // Rodada 1 - FINALIZADA
        partidaRepository.save(Partida.builder()
                .temporada(serieA2024)
                .rodada(1)
                .dataHora(LocalDateTime.of(2024, 4, 13, 18, 30))
                .timeMandante(flamengo)
                .timeVisitante(saopaulo)
                .golsMandante(1)
                .golsVisitante(2)
                .status(StatusPartida.FINALIZADA)
                .local("Maracanã")
                .build());

        // Rodada 2 - AGENDADA
        partidaRepository.save(Partida.builder()
                .temporada(serieA2024)
                .rodada(2)
                .dataHora(LocalDateTime.of(2024, 4, 20, 16, 0))
                .timeMandante(saopaulo)
                .timeVisitante(flamengo)
                .status(StatusPartida.AGENDADA)
                .local("Morumbi")
                .build());

        // Criar partidas placeholder para rodadas 3-38
        for (int rodada = 3; rodada <= 38; rodada++) {
            int timeIdx = (rodada % 2);
            Time mandante = timeIdx == 0 ? flamengo : saopaulo;
            Time visitante = timeIdx == 0 ? saopaulo : flamengo;

            partidaRepository.save(Partida.builder()
                    .temporada(serieA2024)
                    .rodada(rodada)
                    .dataHora(LocalDateTime.of(2024, 4, 20 + (rodada * 7), 16, 0))
                    .timeMandante(mandante)
                    .timeVisitante(visitante)
                    .status(StatusPartida.AGENDADA)
                    .local("Estádio genérico")
                    .build());
        }

        // Criar partidas (Bundesliga 2024/25 - Rodadas 1 a 34)
        // Rodada 1 - FINALIZADA
        partidaRepository.save(Partida.builder()
                .temporada(bundesliga2024)
                .rodada(1)
                .dataHora(LocalDateTime.of(2024, 8, 16, 19, 30))
                .timeMandante(bayernMunique)
                .timeVisitante(borussiaDortmund)
                .golsMandante(3)
                .golsVisitante(1)
                .status(StatusPartida.FINALIZADA)
                .local("Allianz Arena")
                .build());

        // Rodada 2 - ADIADA
        partidaRepository.save(Partida.builder()
                .temporada(bundesliga2024)
                .rodada(2)
                .dataHora(LocalDateTime.of(2024, 8, 24, 15, 30))
                .timeMandante(borussiaDortmund)
                .timeVisitante(bayernMunique)
                .status(StatusPartida.ADIADA)
                .local("Signal Iduna Park")
                .build());

        // Criar partidas placeholder para rodadas 3-34
        for (int rodada = 3; rodada <= 34; rodada++) {
            int timeIdx = (rodada % 2);
            Time mandante = timeIdx == 0 ? bayernMunique : borussiaDortmund;
            Time visitante = timeIdx == 0 ? borussiaDortmund : bayernMunique;

            partidaRepository.save(Partida.builder()
                    .temporada(bundesliga2024)
                    .rodada(rodada)
                    .dataHora(LocalDateTime.of(2024, 8, 24 + (rodada * 7), 19, 30))
                    .timeMandante(mandante)
                    .timeVisitante(visitante)
                    .status(StatusPartida.AGENDADA)
                    .local("Estádio genérico")
                    .build());
        }
    }
}
