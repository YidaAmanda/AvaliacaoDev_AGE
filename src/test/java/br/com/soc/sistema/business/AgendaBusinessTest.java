package br.com.soc.sistema.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.dao.AgendaDao;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class AgendaBusinessTest {

    /*1*/
    private AgendaDao dao;
    private CompromissoDao compromissoDao;
    private AgendaBusiness business;

    @Before
    public void setUp() {
        dao = mock(AgendaDao.class);
        compromissoDao = mock(CompromissoDao.class);
        business = new AgendaBusiness(dao, compromissoDao);
    }

    @Test
    public void nomeNuloLancaExcecao() {
        esperaMensagem(() -> business.salvarAgenda(new AgendaVo()), AgendaBusiness.NOME_OBRIGATORIO);
    }

    @Test
    public void nomeAcimaDe255LancaExcecao() {
        AgendaVo vo = new AgendaVo();
        vo.setNome(repetir("a", 256));
        vo.setPeriodoDisponivel(3);
        esperaMensagem(() -> business.salvarAgenda(vo), AgendaBusiness.NOME_EXCEDEU_LIMITE);
    }

    @Test
    public void periodoInvalidoLancaExcecao() {
        AgendaVo vo = new AgendaVo(); vo.setNome("Cardiologia"); vo.setPeriodoDisponivel(99);
        esperaMensagem(() -> business.salvarAgenda(vo), AgendaBusiness.PERIODO_OBRIGATORIO);
    }

    @Test
    public void nomeComEspacosEhAparadoAntesDeInserir() {
        AgendaVo vo = new AgendaVo(); vo.setNome("  Cardiologia  "); vo.setPeriodoDisponivel(3);
        business.salvarAgenda(vo);
        assertEquals("Cardiologia", vo.getNome());
        verify(dao, times(1)).insertAgenda(vo);
    }

    @Test
    public void excluirAgendaComCompromissosNaoDeleta() {
        when(compromissoDao.existePorAgenda(1L)).thenReturn(true);
        esperaMensagem(() -> business.excluirAgenda(1L), AgendaBusiness.AGENDA_COM_COMPROMISSOS);
        verify(dao, never()).deleteAgenda(anyLong());
    }

    @Test
    public void excluirAgendaLivreChamaDelete() {
        when(compromissoDao.existePorAgenda(1L)).thenReturn(false);
        business.excluirAgenda(1L);
        verify(dao, times(1)).deleteAgenda(1L);
    }
    /*1*/

    /*2*/
    private AgendaDao agendaDaoReal;
    private CompromissoDao compromissoDaoReal;
    private FuncionarioDao funcionarioDaoReal;
    private Long idAgenda;
    private Long idFunc;

    private AgendaBusiness prepararAgendaComCompromisso() {
        agendaDaoReal = new AgendaDao();
        compromissoDaoReal = new CompromissoDao();
        funcionarioDaoReal = new FuncionarioDao();

        long marca = System.nanoTime();

        AgendaVo a = new AgendaVo(); a.setNome("Agenda_" + marca); a.setPeriodoDisponivel(3);
        agendaDaoReal.insertAgenda(a);
        idAgenda = agendaDaoReal.findAllByNome("Agenda_" + marca).get(0).getRowid();

        FuncionarioVo fv = new FuncionarioVo(); fv.setNome("Func_" + marca);
        funcionarioDaoReal.insertFuncionario(fv);
        idFunc = funcionarioDaoReal.findAllByNome("Func_" + marca).get(0).getRowid();

        CompromissoVo c = new CompromissoVo();
        FuncionarioVo f = new FuncionarioVo(); f.setRowid(idFunc);
        AgendaVo ag = new AgendaVo(); ag.setRowid(idAgenda);
        c.setFuncionario(f); c.setAgenda(ag);
        c.setData(LocalDate.now().plusDays(7)); c.setHora(LocalTime.of(10, 0));
        compromissoDaoReal.insertCompromisso(c);

        return new AgendaBusiness(agendaDaoReal, compromissoDaoReal);
    }

    @Test
    public void naoExcluiAgendaComCompromissoReal() {
        AgendaBusiness businessReal = prepararAgendaComCompromisso();
        try {
            businessReal.excluirAgenda(idAgenda);
            fail("esperava BusinessException de agenda com compromissos");
        } catch (BusinessException e) {
            assertEquals(AgendaBusiness.AGENDA_COM_COMPROMISSOS, e.getMessage());
        }
        assertNotNull("a agenda NÃO pode ter sido apagada", agendaDaoReal.findByCodigo(idAgenda));
    }

    @Test(expected = TechnicalException.class)
    public void fkImpedeApagarAgendaComCompromisso() {
        prepararAgendaComCompromisso();
        agendaDaoReal.deleteAgenda(idAgenda);
    }

    @After
    public void limparIntegracao() {
        if (funcionarioDaoReal != null && idFunc != null)
            try { funcionarioDaoReal.deleteFuncionario(idFunc); } catch (Exception ignore) {}
        if (agendaDaoReal != null && idAgenda != null)
            try { agendaDaoReal.deleteAgenda(idAgenda); } catch (Exception ignore) {}
    }
    /*2*/
    
    private String repetir(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    private void esperaMensagem(Runnable acao, String mensagem) {
        try {
            acao.run();
            fail("Esperava BusinessException: " + mensagem);
        } catch (BusinessException e) {
            assertEquals(mensagem, e.getMessage());
        }
    }
}