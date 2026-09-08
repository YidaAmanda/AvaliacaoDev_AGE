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
	public static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	public static final String PERIODO_INVALIDO = "Periodo invalido";
	public static final String DATA_INVALIDA = "Data invalida";
	public static final String FUNCIONARIO_OBRIGATORIO = "Funcionario obrigatorio";
	public static final String AGENDA_OBRIGATORIA = "Agenda obrigatoria";
	public static final String DATA_OBRIGATORIA = "Data obrigatoria";
	public static final String HORA_OBRIGATORIA = "Hora obrigatoria";
	public static final String AGENDA_NAO_ENCONTRADA = "Agenda nao encontrada";
	public static final String FUNCIONARIO_NAO_ENCONTRADO = "Funcionario nao encontrado";
	public static final String PERIODO_DA_AGENDA_INVALIDO = "Periodo da agenda invalido";
	public static final String FUNCIONARIO_COM_CONFLITO = "Funcionario ja possui compromisso nesta data e horario";
	public static final String DATA_NO_PASSADO = "Data/hora do compromisso nao pode estar no passado";
	public static final String FALHA_INCLUSAO = "Nao foi possivel realizar a inclusao do registro";
	public static final String FALHA_EDICAO = "Nao foi possivel realizar a edicao do registro";
	public static final String COMPROMISSO_NAO_ENCONTRADO_PARA_ATUALIZACAO = "Compromisso nao encontrado para atualizacao";
	public static final String FALHA_EXCLUSAO = "Erro ao excluir compromisso";
	
	private CompromissoDao dao;
	private AgendaBusiness agendaBusiness = new AgendaBusiness();
	private FuncionarioBusiness funcionarioBusiness = new FuncionarioBusiness();
	
	public CompromissoBusiness() {
		this.dao = new CompromissoDao();
	}
	
	CompromissoBusiness(CompromissoDao dao, AgendaBusiness agendaBusiness, FuncionarioBusiness funcionarioBusiness) {
		this.dao = dao;
		this.agendaBusiness = agendaBusiness;
		this.funcionarioBusiness = funcionarioBusiness;
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
	                    throw new BusinessException(PERIODO_INVALIDO);
	                compromissos.addAll(dao.findAllByPeriodo(cod));
	            } catch (NumberFormatException e) {
	                throw new BusinessException(PERIODO_INVALIDO);
	            }
	            break;

	        case DATA:
	            try {
	                compromissos.addAll(dao.findAllByData(LocalDate.parse(filter.getBusca().trim())));
	            } catch (DateTimeParseException e) {
	                throw new BusinessException(DATA_INVALIDA);
	            }
	            break;
	    }
	    return compromissos;
	}
	/*1*/
	
	/*2 - 3*/
	public static String horarioForaDoPeriodo(PeriodoDisponivel periodo) {
	    return "Horario fora do periodo disponivel da agenda (" + periodo.getDescricao() + ")";
	}
	
	private void validarENormalizar(CompromissoVo compromissoVo) {
		if (compromissoVo.getFuncionario().getRowid() == null)
			throw new BusinessException(FUNCIONARIO_OBRIGATORIO);
		
		if (compromissoVo.getAgenda().getRowid() == null)
			throw new BusinessException(AGENDA_OBRIGATORIA);
		
		if (compromissoVo.getData() == null)
			throw new BusinessException(DATA_OBRIGATORIA);
		
		if (compromissoVo.getHora() == null)
			throw new BusinessException(HORA_OBRIGATORIA);
		
		AgendaVo agenda = agendaBusiness.buscarAgendaPor(compromissoVo.getAgenda().getRowid());
		if (agenda == null)
		    throw new BusinessException(AGENDA_NAO_ENCONTRADA);

		FuncionarioVo funcionario = funcionarioBusiness.buscarFuncionarioPor(compromissoVo.getFuncionario().getRowid());
		if (funcionario == null)
			throw new BusinessException(FUNCIONARIO_NAO_ENCONTRADO);
		
		PeriodoDisponivel periodo = PeriodoDisponivel.buscarPor(agenda.getPeriodoDisponivel())
		        .orElseThrow(() -> new BusinessException(PERIODO_DA_AGENDA_INVALIDO));

		if (!periodo.contemHorario(compromissoVo.getHora()))
		    throw new BusinessException(horarioForaDoPeriodo(periodo));
		
		if (dao.existeConflito(compromissoVo.getFuncionario().getRowid(), compromissoVo.getData(),
							   compromissoVo.getHora(), compromissoVo.getRowid()))
		    throw new BusinessException(FUNCIONARIO_COM_CONFLITO);
	}
	
	private void validarDataFutura(CompromissoVo compromissoVo) {
		LocalDateTime quando = LocalDateTime.of(compromissoVo.getData(), compromissoVo.getHora());
		if (quando.isBefore(LocalDateTime.now()))
			throw new BusinessException(DATA_NO_PASSADO);
	}
	
	public void salvarCompromisso(CompromissoVo compromissoVo) {
		validarENormalizar(compromissoVo);
		validarDataFutura(compromissoVo);
		
		try {
			dao.insertCompromisso(compromissoVo);
		} catch (Exception e) {
			throw new BusinessException(FALHA_INCLUSAO);
		}
		
	}	
	
	public void atualizarCompromisso(CompromissoVo compromissoVo) {
	    validarENormalizar(compromissoVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateCompromisso(compromissoVo);
	    } catch (Exception e) {
	        throw new BusinessException(FALHA_EDICAO);
	    }

	    if (linhas == 0)
	        throw new BusinessException(COMPROMISSO_NAO_ENCONTRADO_PARA_ATUALIZACAO);
	}
	
	/*2 - 3*/
	
	/*4*/
	public void excluirCompromisso(Long codigo) {
		try {
			dao.deleteCompromisso(codigo);
		}catch (Exception e) {
			throw new BusinessException(FALHA_EXCLUSAO);
		}
	}
	/*4*/	
}
