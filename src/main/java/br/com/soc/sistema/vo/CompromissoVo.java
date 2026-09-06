package br.com.soc.sistema.vo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CompromissoVo {
	private Long rowid;
	private AgendaVo agendaVo;
	private FuncionarioVo funcionarioVo;
	private LocalDate data;
	private LocalTime hora;
	
	public CompromissoVo() {}
		
	public CompromissoVo(Long rowid, AgendaVo agendaVo, FuncionarioVo funcionarioVo, LocalDate data, LocalTime hora) {
		this.rowid = rowid;
		this.agendaVo = agendaVo;
		this.funcionarioVo = funcionarioVo;
		this.data = data;
		this.hora = hora;
	}

	public Long getRowid() {
		return rowid;
	}
	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}

	public FuncionarioVo getFuncionario() {
		return funcionarioVo;
	}
	public void setFuncionario(FuncionarioVo funcionarioVo) {
		this.funcionarioVo = funcionarioVo;
	}
	
	public AgendaVo getAgenda() {
		return agendaVo;
	}
	public void setAgenda(AgendaVo agendaVo) {
		this.agendaVo = agendaVo;
	}
	
	public LocalDate getData() {
		return data;
	}
	public String getDataFormatada() {
	    return data == null ? "" : data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	
	public LocalTime getHora() {
		return hora;
	}
	public String getHoraFormatada() {
	    return hora == null ? "" : hora.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	public void setHora(LocalTime hora) {
		this.hora = hora;
	}
	
	@Override
	public String toString() {
		return "CompromissoVo [rowid=" + rowid + ", funcionario=" + funcionarioVo.getNome() + ", agenda=" + agendaVo.getNome() + ", data=" + data + ", hora=" + hora + "]";
	}
}
