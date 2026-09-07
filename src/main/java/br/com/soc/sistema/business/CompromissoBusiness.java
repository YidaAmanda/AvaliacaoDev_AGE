package br.com.soc.sistema.business;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import br.com.soc.sistema.infra.PeriodoDisponivel;

import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.FuncionarioVo;
import br.com.soc.sistema.filter.CompromissoFilter;

public class CompromissoBusiness {
	private static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	private CompromissoDao dao;
	private AgendaBusiness agendaBusiness = new AgendaBusiness();
	private FuncionarioBusiness funcionarioBusiness = new FuncionarioBusiness();
	
	public CompromissoBusiness() {
		this.dao = new CompromissoDao();
	}
	
	/*1*/
	public List<CompromissoVo> trazerTodosOsCompromissos(){
		return dao.findAllCompromissos();
	}
	
	public CompromissoVo buscarCompromissoPor(Long codigo) {
		return dao.findByCodigo(codigo);
	}
	
	public List<CompromissoVo> filtrarCompromissos(CompromissoFilter filter) {
	    List<CompromissoVo> compromissos = new ArrayList<>();

	    switch (filter.getCriterio()) {
	        case TODOS:
	            compromissos.addAll(dao.findAllCompromissos());
	            break;

	        case CODIGO:
	            try {
	                Long codigo = Long.parseLong(filter.getBusca().trim());
	                CompromissoVo vo = dao.findByCodigo(codigo);
	                if (vo != null) compromissos.add(vo);
	            } catch (NumberFormatException e) {
	                throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	            }
	            break;
	            
	        case CODIGOAGENDA:
	            try {
	                compromissos.addAll(dao.findAllByAgenda(Long.parseLong(filter.getBusca().trim())));
	            } catch (NumberFormatException e) {
	                throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	            }
	            break;

	        case NOMEAGENDA:
	            compromissos.addAll(dao.findAllByNomeAgenda(filter.getBusca().trim()));
	            break;

	        case CODIGOFUNCIONARIO:
	            try {
	                compromissos.addAll(dao.findAllByFuncionario(Long.parseLong(filter.getBusca().trim())));
	            } catch (NumberFormatException e) {
	                throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	            }
	            break;

	        case NOMEFUNCIONARIO:
	            compromissos.addAll(dao.findAllByNomeFuncionario(filter.getBusca().trim()));
	            break;

	        case PERIODO:
	            try {
	                Integer cod = Integer.parseInt(filter.getBusca().trim());
	                if (!PeriodoDisponivel.buscarPor(cod).isPresent())
	                    throw new BusinessException("Periodo invalido");
	                compromissos.addAll(dao.findAllByPeriodo(cod));
	            } catch (NumberFormatException e) {
	                throw new BusinessException("Periodo invalido");
	            }
	            break;

	        case DATA:
	            try {
	                compromissos.addAll(dao.findAllByData(LocalDate.parse(filter.getBusca().trim())));
	            } catch (DateTimeParseException e) {
	                throw new BusinessException("Data invalida");
	            }
	            break;
	    }
	    return compromissos;
	}
	/*1*/
	
	/*2 - 3*/
	private void validarENormalizar(CompromissoVo compromissoVo) {
		if (compromissoVo.getFuncionario().getRowid() == null)
			throw new BusinessException("Funcionario obrigatorio");
		
		if (compromissoVo.getAgenda().getRowid() == null)
			throw new BusinessException("Agenda obrigatoria");
		
		if (compromissoVo.getData() == null)
			throw new BusinessException("Data nao pode ser nula");
		
		if (compromissoVo.getHora() == null)
			throw new BusinessException("Hora nao pode ser nula");
		
		AgendaVo agenda = agendaBusiness.buscarAgendaPor(compromissoVo.getAgenda().getRowid());
		if (agenda == null)
		    throw new BusinessException("Agenda nao encontrada");

		FuncionarioVo funcionario = funcionarioBusiness.buscarFuncionarioPor(compromissoVo.getFuncionario().getRowid());
		if (funcionario == null)
			throw new BusinessException("Funcionario nao encontrada");
		
		PeriodoDisponivel periodo = PeriodoDisponivel.buscarPor(agenda.getPeriodoDisponivel())
		        .orElseThrow(() -> new BusinessException("Periodo da agenda invalido"));

		if (!periodo.contemHorario(compromissoVo.getHora()))
		    throw new BusinessException("Horario fora do periodo disponivel da agenda (" + periodo.getDescricao() + ")");
		
		if (dao.existeConflito(compromissoVo.getFuncionario().getRowid(), compromissoVo.getData(),
							   compromissoVo.getHora(), compromissoVo.getRowid()))
		    throw new BusinessException("Funcionario ja possui compromisso nesta data e horario");
	}
	
	private void validarDataFutura(CompromissoVo compromissoVo) {
		LocalDateTime quando = LocalDateTime.of(compromissoVo.getData(), compromissoVo.getHora());
		if (quando.isBefore(LocalDateTime.now()))
			throw new BusinessException("Data/hora do compromisso nao pode estar no passado");
	}
	
	public void salvarCompromisso(CompromissoVo compromissoVo) {
		validarENormalizar(compromissoVo);
		validarDataFutura(compromissoVo);
		
		try {
			dao.insertCompromisso(compromissoVo);
		} catch (Exception e) {
			throw new BusinessException("Nao foi possivel realizar a inclusao do registro");
		}
		
	}	
	
	public void atualizarCompromisso(CompromissoVo compromissoVo) {
	    validarENormalizar(compromissoVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateCompromisso(compromissoVo);
	    } catch (Exception e) {
	        throw new BusinessException("Nao foi possivel realizar a edicao do registro");
	    }

	    if (linhas == 0)
	        throw new BusinessException("Compromisso nao encontrada para atualizacao");
	}
	
	/*2 - 3*/
	
	/*4*/
	public void excluirCompromisso(Long codigo) {
		try {
			dao.deleteCompromisso(codigo);
		}catch (Exception e) {
			throw new BusinessException("Erro ao excluir compromisso");
		}
	}
	/*4*/	
}
