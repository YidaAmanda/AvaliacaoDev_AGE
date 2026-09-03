package br.com.soc.sistema.infra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PeriodoDisponivel {
	MANHA(1, "Manhã"), 
	TARDE(2, "Tarde"),
	AMBOS(3, "Ambos");

	private Integer codigo;
	private String descricao;
	private final static Map<Integer, PeriodoDisponivel> opcoes = new HashMap<>();
	
	static {
		Arrays.asList(PeriodoDisponivel.values())
		.forEach(
			opcao -> opcoes.put(opcao.getCodigo(), opcao)
		);
	}
	
	private PeriodoDisponivel(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
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