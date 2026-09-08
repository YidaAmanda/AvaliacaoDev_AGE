package br.com.soc.sistema.business;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.filter.CompromissoFilter;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class CompromissoBusinessTest {

    private CompromissoDao dao;
    private AgendaBusiness agendaBusiness;
    private FuncionarioBusiness funcionarioBusiness;
    private CompromissoBusiness business;

    @Before
    public void setUp() {
        dao = mock(CompromissoDao.class);
        agendaBusiness = mock(AgendaBusiness.class);
        funcionarioBusiness = mock(FuncionarioBusiness.class);
        business = new CompromissoBusiness(dao, agendaBusiness, funcionarioBusiness);
    }
    
    @Test(expected = BusinessException.class)
    public void salvarSemFuncionarioLancaExcecao() {
        CompromissoVo vo = compromissoValido();
        vo.setFuncionario(new FuncionarioVo());
        business.salvarCompromisso(vo);
    }

    @Test
    public void salvarValidoChamaInsertUmaVez() {
        CompromissoVo vo = compromissoValido();
        when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaAmbos());
        when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(new FuncionarioVo());
        when(dao.existeConflito(any(), any(), any(), any())).thenReturn(false);

        business.salvarCompromisso(vo);

        verify(dao, times(1)).insertCompromisso(vo);
    }

    @Test
    public void salvarComConflitoNaoPersiste() {
        CompromissoVo vo = compromissoValido();
        when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaAmbos());
        when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(new FuncionarioVo());
        when(dao.existeConflito(any(), any(), any(), any())).thenReturn(true);

        try { business.salvarCompromisso(vo); } catch (BusinessException ok) { /* esperado */ }

        verify(dao, never()).insertCompromisso(any());
    }

	
	@Test
	public void agendaNaoEncontradaLancaExcecao() {
	   CompromissoVo vo = compromissoValido();
	   when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(null);
	   esperaMensagem(() -> business.salvarCompromisso(vo), CompromissoBusiness.AGENDA_NAO_ENCONTRADA);
	}
	
	@Test
	public void funcionarioNaoEncontradoLancaExcecao() {
	   CompromissoVo vo = compromissoValido();
	   when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaAmbos());
	   when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(null);
	   esperaMensagem(() -> business.salvarCompromisso(vo), CompromissoBusiness.FUNCIONARIO_NAO_ENCONTRADO);
	}
	
	@Test
	public void horarioForaDoPeriodoLancaExcecao() {
	   CompromissoVo vo = compromissoValido();
	   vo.setHora(LocalTime.of(13, 0));
	   when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaComPeriodo(1));
	   when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(new FuncionarioVo());
	   esperaMensagem(() -> business.salvarCompromisso(vo), CompromissoBusiness.horarioForaDoPeriodo(PeriodoDisponivel.MANHA));
	}
	
	@Test
	public void dataNoPassadoLancaExcecao() {
	   CompromissoVo vo = compromissoValido();
	   vo.setData(LocalDate.now().minusDays(1));
	   when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaAmbos());
	   when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(new FuncionarioVo());
	   when(dao.existeConflito(any(), any(), any(), any())).thenReturn(false);
	   esperaMensagem(() -> business.salvarCompromisso(vo), CompromissoBusiness.DATA_NO_PASSADO);
	}
	
	@Test
	public void atualizarSemLinhaAfetadaLancaExcecao() {
	   CompromissoVo vo = compromissoValido();
	   when(agendaBusiness.buscarAgendaPor(anyLong())).thenReturn(agendaAmbos());
	   when(funcionarioBusiness.buscarFuncionarioPor(anyLong())).thenReturn(new FuncionarioVo());
	   when(dao.existeConflito(any(), any(), any(), any())).thenReturn(false);
	   when(dao.updateCompromisso(vo)).thenReturn(0);
	   esperaMensagem(() -> business.atualizarCompromisso(vo), CompromissoBusiness.COMPROMISSO_NAO_ENCONTRADO_PARA_ATUALIZACAO);
	}
	
	@Test
	public void filtrarPorCodigoNaoNumericoLancaExcecao() {
	   CompromissoFilter f = new CompromissoFilter();
	   f.setCriterio(CompromissoFilter.Criterio.CODIGO);
	   f.setBusca("abc");
	   esperaMensagem(() -> business.filtrarCompromissos(f), CompromissoBusiness.FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	}
	
	private AgendaVo agendaComPeriodo(int periodo) {
	   AgendaVo a = new AgendaVo(); a.setRowid(1L); a.setPeriodoDisponivel(periodo); return a;
	}
	
	private void esperaMensagem(Runnable acao, String mensagem) {
	   try {
	       acao.run();
	       fail("Esperava BusinessException: " + mensagem);
	   } catch (BusinessException e) {
	       assertEquals(mensagem, e.getMessage());
	   }
	}

    private CompromissoVo compromissoValido() {
        FuncionarioVo f = new FuncionarioVo(); f.setRowid(1L);
        AgendaVo a = new AgendaVo(); a.setRowid(1L);
        CompromissoVo vo = new CompromissoVo();
        vo.setFuncionario(f);
        vo.setAgenda(a);
        vo.setData(LocalDate.now().plusDays(10));
        vo.setHora(LocalTime.of(9, 0));
        return vo;
    }
    
    private AgendaVo agendaAmbos() {
        AgendaVo a = new AgendaVo(); a.setRowid(1L); a.setPeriodoDisponivel(3); return a;
    }
}