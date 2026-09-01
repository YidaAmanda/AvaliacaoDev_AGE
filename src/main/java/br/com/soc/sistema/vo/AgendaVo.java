package br.com.soc.sistema.vo;

import br.com.soc.sistema.infra.PeriodoDisponivel; 

public class AgendaVo {
	private String rowid;
	private String nome;
	private String periodoDisponivel; //1 - 2 - 3
	
	public AgendaVo() {}
		
	public AgendaVo(String rowid, String nome, String periodoDisponivel) {
		this.rowid = rowid;
		this.nome = nome;
		this.periodoDisponivel = periodoDisponivel;
	}

	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getPeriodoDisponivel() {
		return periodoDisponivel;
	}
	
	public void setPeriodoDisponivel(String periodoDisponivel) {
		this.periodoDisponivel = periodoDisponivel;
	}
	
	public String getDescricaoPeriodo() {
	    return PeriodoDisponivel.buscarPor(periodoDisponivel)
	            .map(PeriodoDisponivel::getDescricao)
	            .orElse("");
	}
	
	@Override
	public String toString() {
		return "AgendaVo [rowid=" + rowid + ", nome=" + nome + ", periodoDisponivel=" + periodoDisponivel + "]";
	}
}
