package br.com.soc.sistema.business;

import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.AgendaDao;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.AgendaFilter;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.FuncionarioVo;
import br.com.soc.sistema.infra.PeriodoDisponivel;

public class AgendaBusiness {
	private static final int NOME_TAMANHO_MAXIMO = 255;
	private static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	private AgendaDao dao;
	
	public AgendaBusiness() {
		this.dao = new AgendaDao();
	}
	
	public List<AgendaVo> trazerTodasAsAgendas(){
		return dao.findAllAgendas();
	}	
	
	private void validarENormalizar(AgendaVo agendaVo) {
		if (agendaVo.getNome() == null)
			throw new BusinessException("Nome nao pode ser nulo");
		
		String nome = agendaVo.getNome().trim();
				
		if(nome.isEmpty())
				throw new BusinessException("Nome nao pode ser em branco");
		
		if(nome.length() > NOME_TAMANHO_MAXIMO)
			throw new BusinessException("Nome deve ter no maximo " + NOME_TAMANHO_MAXIMO + " caracteres");
		
		agendaVo.setNome(nome);
		
		if(!PeriodoDisponivel.buscarPor(agendaVo.getPeriodoDisponivel()).isPresent())
			throw new BusinessException("Periodo obrigatorio");
	}
	
	public void salvarAgenda(AgendaVo agendaVo) {
		validarENormalizar(agendaVo);
		
		try {
			dao.insertAgenda(agendaVo);
		} catch (Exception e) {
			throw new BusinessException("Nao foi possivel realizar a inclusao do registro");
		}
		
	}	
	
	public void atualizarAgenda(AgendaVo agendaVo) {
	    validarENormalizar(agendaVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateAgenda(agendaVo);
	    } catch (Exception e) {
	        throw new BusinessException("Nao foi possivel realizar a edicao do registro");
	    }

	    if (linhas == 0)
	        throw new BusinessException("Agenda nao encontrada para atualizacao");
	}
	
	public void excluirAgenda(Long codigo) {
		if (new CompromissoDao().existePorAgenda(codigo))
		    throw new BusinessException("Agenda possui compromissos e não pode ser excluída");
		
		try {
			dao.deleteAgenda(codigo);
		}catch (Exception e) {
			throw new BusinessException("Erro ao excluir agenda");
		}
	}
	
	public List<AgendaVo> filtrarAgendas(AgendaFilter filter) {
	    List<AgendaVo> agendas = new ArrayList<>();

	    switch (filter.getCriterio()) {
	        case TODOS:
	            agendas.addAll(dao.findAllAgendas());
	            break;

	        case CODIGO:
	            try {
	                Long codigo = Long.parseLong(filter.getBusca().trim());
	                AgendaVo vo = dao.findByCodigo(codigo);
	                if (vo != null) agendas.add(vo);
	            } catch (NumberFormatException e) {
	                throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	            }
	            break;

	        case NOME:
	            agendas.addAll(dao.findAllByNome(filter.getBusca().trim()));
	            break;

	        case PERIODO:
	            try {
	                Integer cod = Integer.parseInt(filter.getBusca().trim());
	                if (!PeriodoDisponivel.buscarPor(cod).isPresent())
	                    throw new BusinessException("Periodo invalido");
	                agendas.addAll(dao.findAllByPeriodo(cod));
	            } catch (NumberFormatException e) {
	                throw new BusinessException("Periodo invalido");
	            }
	            break;
	    }
	    return agendas;
	}
	
	public AgendaVo buscarAgendaPor(Long codigo) {
		return dao.findByCodigo(codigo);
	}
}
