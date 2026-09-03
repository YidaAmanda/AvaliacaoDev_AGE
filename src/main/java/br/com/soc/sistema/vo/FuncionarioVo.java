package br.com.soc.sistema.vo;

public class FuncionarioVo {
	private Long rowid;
	private String nome;	
	
	public FuncionarioVo() {}
		
	public FuncionarioVo(Long rowid, String nome) {
		this.rowid = rowid;
		this.nome = nome;
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
	
	@Override
	public String toString() {
		return "FuncionarioVo [rowid=" + rowid + ", nome=" + nome + "]";
	}
}
