package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.business.CompromissoBusiness;
import br.com.soc.sistema.filter.CompromissoFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.business.AgendaBusiness;

public class CompromissoAction extends Action {
	
	private List<CompromissoVo> compromissos = new ArrayList<>();
	private CompromissoBusiness business = new CompromissoBusiness();
	private CompromissoFilter filtrar = new CompromissoFilter();
	private CompromissoVo compromissoVo = new CompromissoVo();
	
	public String todos() {
		compromissos.addAll(business.trazerTodosOsCompromissos());	

		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.semCriterioDeBusca())
			return REDIRECT;
		
		try {
	        compromissos = business.filtrarCompromissos(filtrar);
	        
	        if (compromissos.isEmpty())
	            addActionMessage("Nenhum compromisso encontrado");
	        
	        return SUCCESS;
	    } catch (BusinessException e) {
	        addActionError(e.getMessage());
	    }
		
		return SUCCESS;
	}
	
	public String novo() {
		return INPUT;
	}
	
	public String criar() {
		try {
			business.salvarCompromisso(compromissoVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String editar() {
		if(compromissoVo.getRowid() == null)
			return REDIRECT;
		
		try {
			compromissoVo = business.buscarCompromissoPor(compromissoVo.getRowid());
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		if(compromissoVo == null) {
			//addActionError("Compromisso nao encontrado");
			return REDIRECT;
		}
			
		return INPUT;
	}
	
	public String deletar() {
	    if (compromissoVo.getRowid() == null)
	        return REDIRECT;
	    
	    try {
	        business.excluirCompromisso(compromissoVo.getRowid());
	    } catch (Exception e) {
	        addActionError(e.getMessage());
	        compromissos.addAll(business.trazerTodosOsCompromissos());
	        return SUCCESS;
	    }
	    return REDIRECT;
	}
	
	public String atualizar() {
		if(compromissoVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.atualizarCompromisso(compromissoVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
	    return REDIRECT;
	}
	
	public List<CompromissoFilter.Criterio> getListaCriterios() {
	    return Arrays.asList(CompromissoFilter.Criterio.values());
	}
	
	public List<PeriodoDisponivel> getListaPeriodos(){
		return Arrays.asList(PeriodoDisponivel.values());
	}
	
	public List<FuncionarioVo> getListaFuncionarios() {
	    return new FuncionarioBusiness().trazerTodosOsFuncionarios();
	}

	public List<AgendaVo> getListaAgendas() {
	    return new AgendaBusiness().trazerTodasAsAgendas();
	}
	
	public List<CompromissoVo> getCompromissos() {
		return compromissos;
	}

	public void setCompromissos(List<CompromissoVo> compromissos) {
		this.compromissos = compromissos;
	}

	public CompromissoFilter getFiltrar() {
		return filtrar;
	}

	public void setFiltrar(CompromissoFilter filtrar) {
		this.filtrar = filtrar;
	}

	public CompromissoVo getCompromissoVo() {
		return compromissoVo;
	}

	public void setCompromissoVo(CompromissoVo compromissoVo) {
		this.compromissoVo = compromissoVo;
	}
}
