package br.com.soc.sistema.filter;

import br.com.soc.sistema.infra.OpcoesComboBuscar;

public class FuncionarioFilter {
	private OpcoesComboBuscar opcoesCombo;
	private String valorBusca;

	public String getValorBusca() {
		return valorBusca;
	}

	public FuncionarioFilter setValorBusca(String valorBusca) {
		this.valorBusca = valorBusca;
		return this;
	}

	public OpcoesComboBuscar getOpcoesCombo() {
		return opcoesCombo;
	}

	public FuncionarioFilter setOpcoesCombo(String codigo) {
		this.opcoesCombo = OpcoesComboBuscar.buscarPor(codigo).orElse(null);
		return this;
	}	
	
	public boolean semCriterioDeBusca() {
	    return opcoesCombo == null
	        || valorBusca == null || valorBusca.trim().isEmpty();
	}
	
	public static FuncionarioFilter builder() {
		return new FuncionarioFilter();
	}
}
