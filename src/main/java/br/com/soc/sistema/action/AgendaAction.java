package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.business.AgendaBusiness;
import br.com.soc.sistema.filter.AgendaFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.AgendaVo;

public class AgendaAction extends Action {
	private List<AgendaVo> agendas = new ArrayList<>();
	private AgendaBusiness business = new AgendaBusiness();
	private AgendaFilter filtrar = new AgendaFilter();
	private AgendaVo agendaVo = new AgendaVo();
	
	/*1*/
	public String todos() {
		agendas.addAll(business.trazerTodasAsAgendas());	

		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.semCriterioDeBusca())
			return REDIRECT;
		
		try {
	        agendas = business.filtrarAgendas(filtrar);
	        
	        if (agendas.isEmpty())
	            addActionMessage("Nenhuma agenda encontrada");
	        
	        return SUCCESS;
	    } catch (BusinessException e) {
	        addActionError(e.getMessage());
	    }
		
		return SUCCESS;
	}
	/*1*/
	
	/*2*/
	public String novo() {
		return INPUT;
	}
	
	public String criar() {
		try {
			business.salvarAgenda(agendaVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	/*2*/
	
	/*3*/
	public String editar() {
		if(agendaVo.getRowid() == null)
			return REDIRECT;
		
		try {
			agendaVo = business.buscarAgendaPor(agendaVo.getRowid());
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		if(agendaVo == null) {
			//addActionError("Agenda nao encontrado");
			return REDIRECT;
		}
			
		return INPUT;
	}

	public String atualizar() {
		if(agendaVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.atualizarAgenda(agendaVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
	    return REDIRECT;
	}
	/*3*/
	
	/*4*/
	public String deletar() {
	    if (agendaVo.getRowid() == null)
	        return REDIRECT;
	    
	    try {
	        business.excluirAgenda(agendaVo.getRowid());
	    } catch (Exception e) {
	        addActionError(e.getMessage());
	        agendas.addAll(business.trazerTodasAsAgendas());
	        return SUCCESS;
	    }
	    return REDIRECT;
	}
	/*4*/
	
	public List<AgendaFilter.Criterio> getListaCriterios() {
	    return Arrays.asList(AgendaFilter.Criterio.values());
	}
	
	public List<PeriodoDisponivel> getListaPeriodos(){
		return Arrays.asList(PeriodoDisponivel.values());
	}
	
	public List<AgendaVo> getAgendas() {
		return agendas;
	}
	public void setAgendas(List<AgendaVo> agendas) {
		this.agendas = agendas;
	}

	public AgendaFilter getFiltrar() {
		return filtrar;
	}
	public void setFiltrar(AgendaFilter filtrar) {
		this.filtrar = filtrar;
	}

	public AgendaVo getAgendaVo() {
		return agendaVo;
	}
	public void setAgendaVo(AgendaVo agendaVo) {
		this.agendaVo = agendaVo;
	}
}
