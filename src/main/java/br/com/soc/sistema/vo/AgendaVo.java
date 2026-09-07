package br.com.soc.sistema.vo;

import br.com.soc.sistema.infra.PeriodoDisponivel; 

public class AgendaVo {
	private Long rowid;
	private String nome;
	private Integer periodoDisponivel; //1 - 2 - 3
	
	public AgendaVo() {}
		
	public AgendaVo(Long rowid, String nome, Integer periodoDisponivel) {
		this.rowid = rowid;
		this.nome = nome;
		this.periodoDisponivel = periodoDisponivel;
	}

	public Long getRowid() {
		return rowid;
	}
	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Integer getPeriodoDisponivel() {
		return periodoDisponivel;
	}
	public String getDescricaoPeriodo() {
	    return PeriodoDisponivel.buscarPor(periodoDisponivel)
	            .map(PeriodoDisponivel::getDescricao)
	            .orElse("");
	}
	public void setPeriodoDisponivel(Integer periodoDisponivel) {
		this.periodoDisponivel = periodoDisponivel;
	}

	@Override
	public String toString() {
		return "AgendaVo [rowid=" + rowid + ", nome=" + nome + ", periodoDisponivel=" + periodoDisponivel + "]";
	}
}
