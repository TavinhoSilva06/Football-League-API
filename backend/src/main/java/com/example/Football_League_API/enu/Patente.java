package com.example.Football_League_API.enu;

public enum Patente {
    BRONZE(0, "Bronze"),
    PRATA(1000, "Prata"),
    OURO(2000, "Ouro"),
    PLATINA(3000, "Platina"),
    DIAMANTE(4000, "Diamante"),
    PERITO(5000, "Perito");

    private final int pontosMinimos;
    private final String descricao;

    Patente(int pontosMinimos, String descricao) {
        this.pontosMinimos = pontosMinimos;
        this.descricao = descricao;
    }

    public int getPontosMinimos() {
        return pontosMinimos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPontosPorAcerto() {
        return 20 - (2 * ordinal());
    }

    public Patente proxima() {
        int proximo = ordinal() + 1;
        return proximo < values().length ? values()[proximo] : null;
    }

    public int pontosParaProxima(int pontos) {
        Patente destino = proxima();
        return destino == null ? 0 : Math.max(0, destino.pontosMinimos - pontos);
    }

    public static Patente fromPontos(int pontos) {
        Patente atual = BRONZE;
        for (Patente candidata : values()) {
            if (pontos < candidata.pontosMinimos) {
                break;
            }
            atual = candidata;
        }
        return atual;
    }
}
