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
	public static final String NOME_EXCEDEU_LIMITE = "Nome deve ter no maximo " + NOME_TAMANHO_MAXIMO + " caracteres";
	
	public static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	public static final String PERIODO_INVALIDO = "Periodo invalido";
	public static final String NOME_OBRIGATORIO = "Nome obrigatorio";
	public static final String NOME_EM_BRANCO = "Nome nao pode ser em branco";
	public static final String PERIODO_OBRIGATORIO = "Periodo obrigatorio";
	public static final String FALHA_INCLUSAO = "Nao foi possivel realizar a inclusao do registro";
	public static final String FALHA_EDICAO = "Nao foi possivel realizar a edicao do registro";
	public static final String AGENDA_NAO_ENCONTRADA_PARA_ATUALIZACAO = "Agenda nao encontrada para atualizacao";
	public static final String AGENDA_COM_COMPROMISSOS = "Agenda possui compromissos e nao pode ser excluida";
	public static final String FALHA_EXCLUSAO = "Erro ao excluir agenda";
	public static final String CODIGO_OBRIGATORIO = "Codigo obrigatorio";
	
	private AgendaDao dao;
	private CompromissoDao compromissoDao;

	public AgendaBusiness() {
		this(new AgendaDao(), new CompromissoDao());
	}
	
	AgendaBusiness(AgendaDao dao, CompromissoDao compromissoDao) {
		this.dao = dao;
		this.compromissoDao = compromissoDao;
	}
	
	/*1*/
	public List<AgendaVo> trazerTodasAsAgendas(){
		return dao.findAllAgendas();
	}
	
	public AgendaVo buscarAgendaPor(Long codigo) {
		if (codigo == null)
	        throw new BusinessException(CODIGO_OBRIGATORIO);
		
		return dao.findByCodigo(codigo);
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
	                    throw new BusinessException(PERIODO_INVALIDO);
	                agendas.addAll(dao.findAllByPeriodo(cod));
	            } catch (NumberFormatException e) {
	                throw new BusinessException(PERIODO_INVALIDO);
	            }
	            break;
	    }
	    return agendas;
	}
	/*1*/
	
	/*2 - 3*/
	private void validarENormalizar(AgendaVo agendaVo) {
		if (agendaVo.getNome() == null)
			throw new BusinessException(NOME_OBRIGATORIO);
		
		String nome = agendaVo.getNome().trim();
				
		if(nome.isEmpty())
				throw new BusinessException(NOME_EM_BRANCO);
		
		if(nome.length() > NOME_TAMANHO_MAXIMO)
			throw new BusinessException(NOME_EXCEDEU_LIMITE);
		
		agendaVo.setNome(nome);
		
		if(!PeriodoDisponivel.buscarPor(agendaVo.getPeriodoDisponivel()).isPresent())
			throw new BusinessException(PERIODO_OBRIGATORIO);
	}
	
	public void salvarAgenda(AgendaVo agendaVo) {
		validarENormalizar(agendaVo);
		
		try {
			dao.insertAgenda(agendaVo);
		} catch (Exception e) {
			throw new BusinessException(FALHA_INCLUSAO);
		}
		
	}	
	
	public void atualizarAgenda(AgendaVo agendaVo) {
	    validarENormalizar(agendaVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateAgenda(agendaVo);
	    } catch (Exception e) {
	        throw new BusinessException(FALHA_EDICAO);
	    }

	    if (linhas == 0)
	        throw new BusinessException(AGENDA_NAO_ENCONTRADA_PARA_ATUALIZACAO);
	}
	/*2 - 3*/
	
	/*4*/
	public void excluirAgenda(Long codigo) {
		if (codigo == null)
	        throw new BusinessException(CODIGO_OBRIGATORIO);
		
		if (compromissoDao.existePorAgenda(codigo))
		    throw new BusinessException(AGENDA_COM_COMPROMISSOS);
		
		try {
			dao.deleteAgenda(codigo);
		}catch (Exception e) {
			throw new BusinessException(FALHA_EXCLUSAO);
		}
	}
	/*4*/
}
