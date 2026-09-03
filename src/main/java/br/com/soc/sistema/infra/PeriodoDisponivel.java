package br.com.soc.sistema.infra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.LocalTime;

public enum PeriodoDisponivel {
	MANHA(1, "Manhã", LocalTime.MIN, LocalTime.of(11, 59, 59)),
	TARDE(2, "Tarde", LocalTime.of(12, 0), LocalTime.MAX),
	AMBOS(3, "Ambos", LocalTime.MIN, LocalTime.MAX);

	private Integer codigo;
	private String descricao;
	private final LocalTime inicio;
	private final LocalTime fim;
	private final static Map<Integer, PeriodoDisponivel> opcoes = new HashMap<>();

	private PeriodoDisponivel(Integer codigo, String descricao, LocalTime inicio, LocalTime fim) {
	    this.codigo = codigo;
	    this.descricao = descricao;
	    this.inicio = inicio;
	    this.fim = fim;
	}

	public boolean contemHorario(LocalTime hora) {
	    return hora != null && !hora.isBefore(inicio) && !hora.isAfter(fim);
	}
	
	static {
		Arrays.asList(PeriodoDisponivel.values())
		.forEach(
			opcao -> opcoes.put(opcao.getCodigo(), opcao)
		);
	}
	
	public static Optional<PeriodoDisponivel> buscarPor(Integer codigo) {
	    return getOpcao(codigo);
	}
	
	private static Optional<PeriodoDisponivel> getOpcao(Integer codigo){
		return Optional.ofNullable(opcoes.get(codigo));
	}
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
}