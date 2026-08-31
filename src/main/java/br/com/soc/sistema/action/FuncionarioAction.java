package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.filter.FuncionarioFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.OpcoesComboBuscar;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioAction extends Action {
	
	private List<FuncionarioVo> funcionarios = new ArrayList<>();
	private FuncionarioBusiness business = new FuncionarioBusiness();
	private FuncionarioFilter filtrar = new FuncionarioFilter();
	private FuncionarioVo funcionarioVo = new FuncionarioVo();
	
	public String todos() {
		funcionarios.addAll(business.trazerTodosOsFuncionarios());	

		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.semCriterioDeBusca())
			return REDIRECT;
		
		try {
	        funcionarios = business.filtrarFuncionarios(filtrar);
	        
	        if (funcionarios.isEmpty())
	            addActionMessage("Nenhum funcionário encontrado");
	        
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
			business.salvarFuncionario(funcionarioVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String editar() {
		if(funcionarioVo.getRowid() == null)
			return REDIRECT;
		
		try {
			funcionarioVo = business.buscarFuncionarioPor(funcionarioVo.getRowid());
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		if(funcionarioVo == null) {
			//addActionError("Funcionario nao encontrado");
			return REDIRECT;
		}
			
		return INPUT;
	}
	
	public String deletar() {
		if(funcionarioVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.excluirFuncionario(funcionarioVo.getRowid());
		} catch (Exception e) {
			addActionError(e.getMessage());
		}

		return REDIRECT;
	}
	
	public String atualizar() {
		if(funcionarioVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.atualizarFuncionario(funcionarioVo);
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
	    return REDIRECT;
	}
	
	public List<OpcoesComboBuscar> getListaOpcoesCombo(){
		return Arrays.asList(OpcoesComboBuscar.values());
	}
	
	public List<FuncionarioVo> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<FuncionarioVo> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public FuncionarioFilter getFiltrar() {
		return filtrar;
	}

	public void setFiltrar(FuncionarioFilter filtrar) {
		this.filtrar = filtrar;
	}

	public FuncionarioVo getFuncionarioVo() {
		return funcionarioVo;
	}

	public void setFuncionarioVo(FuncionarioVo funcionarioVo) {
		this.funcionarioVo = funcionarioVo;
	}
}
