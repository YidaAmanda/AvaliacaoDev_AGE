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
	public static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um número";
	public static final String PERIODO_INVALIDO = "Período inválido";
	public static final String DATA_INVALIDA = "Data inválida";
	public static final String FUNCIONARIO_OBRIGATORIO = "Funcionário obrigatório";
	public static final String AGENDA_OBRIGATORIA = "Agenda obrigatória";
	public static final String DATA_OBRIGATORIA = "Data obrigatória";
	public static final String HORA_OBRIGATORIA = "Hora obrigatória";
	public static final String AGENDA_NAO_ENCONTRADA = "Agenda não encontrada";
	public static final String FUNCIONARIO_NAO_ENCONTRADO = "Funcionário não encontrado";
	public static final String PERIODO_DA_AGENDA_INVALIDO = "Período da agenda inválido";
	public static final String FUNCIONARIO_COM_CONFLITO = "Funcionário ja possui compromisso nesta data e horário";
	public static final String DATA_NO_PASSADO = "Data/hora do compromisso não pode estar no passado";
	public static final String FALHA_INCLUSAO = "Não foi possível realizar a inclusão do registro";
	public static final String FALHA_EDICAO = "Não foi possível realizar a edição do registro";
	public static final String COMPROMISSO_NAO_ENCONTRADO_PARA_ATUALIZACAO = "Compromisso não encontrado para atualização";
	public static final String FALHA_EXCLUSAO = "Erro ao excluir compromisso";
	public static final String CODIGO_OBRIGATORIO = "Código obrigatório";
	
	private CompromissoDao dao;
	private AgendaBusiness agendaBusiness;
	private FuncionarioBusiness funcionarioBusiness;

	public CompromissoBusiness() {
		this(new CompromissoDao(), new AgendaBusiness(), new FuncionarioBusiness());
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
		if (codigo == null)
	        throw new BusinessException(CODIGO_OBRIGATORIO);
		
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
	
	private void validar(CompromissoVo compromissoVo) {
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
		validar(compromissoVo);
		validarDataFutura(compromissoVo);
		
		try {
			dao.insertCompromisso(compromissoVo);
		} catch (Exception e) {
			throw new BusinessException(FALHA_INCLUSAO);
		}
		
	}	
	
	public void atualizarCompromisso(CompromissoVo compromissoVo) {
	    validar(compromissoVo);

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
		if (codigo == null)
	        throw new BusinessException(CODIGO_OBRIGATORIO);
		
		try {
			dao.deleteCompromisso(codigo);
		}catch (Exception e) {
			throw new BusinessException(FALHA_EXCLUSAO);
		}
	}
	/*4*/	
}
