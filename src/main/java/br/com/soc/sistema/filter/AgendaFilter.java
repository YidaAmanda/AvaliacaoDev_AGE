package br.com.soc.sistema.filter;

import br.com.soc.sistema.infra.OpcoesComboBuscar;

public class AgendaFilter {
	private OpcoesComboBuscar opcoesCombo;
	private String valorBusca;

	public String getValorBusca() {
		return valorBusca;
	}

	public AgendaFilter setValorBusca(String valorBusca) {
		this.valorBusca = valorBusca;
		return this;
	}

	public OpcoesComboBuscar getOpcoesCombo() {
		return opcoesCombo;
	}

	public AgendaFilter setOpcoesCombo(String codigo) {
		this.opcoesCombo = OpcoesComboBuscar.buscarPor(codigo).orElse(null);
		return this;
	}	
	
	public boolean semCriterioDeBusca() {
	    return opcoesCombo == null
	        || valorBusca == null || valorBusca.trim().isEmpty();
	}
}
