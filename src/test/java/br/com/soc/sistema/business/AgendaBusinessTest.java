package br.com.soc.sistema.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.dao.AgendaDao;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.AgendaVo;

public class AgendaBusinessTest {

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