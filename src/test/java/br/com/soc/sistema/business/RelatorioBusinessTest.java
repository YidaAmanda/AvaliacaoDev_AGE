package br.com.soc.sistema.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioBusinessTest {

    private CompromissoDao dao;
    private RelatorioBusiness business;

    @Before
    public void setUp() {
        dao = mock(CompromissoDao.class);
        business = new RelatorioBusiness(dao);
    }

    @Test
    public void datasNulasLancaExcecao() {
        esperaMensagem(() -> business.filtrarPorPeriodo(null, LocalDate.now()),
                       RelatorioBusiness.DATAS_OBRIGATORIAS);
        verify(dao, never()).findPorPeriodo(any(), any());
    }

    @Test
    public void dataInicialDepoisDaFinalLancaExcecao() {
        LocalDate ini = LocalDate.of(2026, 5, 10);
        LocalDate fim = LocalDate.of(2026, 5, 1);
        esperaMensagem(() -> business.filtrarPorPeriodo(ini, fim),
                       RelatorioBusiness.DATA_INICIAL_POSTERIOR_A_FINAL);
    }

    @Test
    public void mesmaDataEhPermitida() {
        LocalDate dia = LocalDate.of(2026, 5, 10);
        when(dao.findPorPeriodo(dia, dia)).thenReturn(new ArrayList<>());
        business.filtrarPorPeriodo(dia, dia);
        verify(dao, times(1)).findPorPeriodo(dia, dia);
    }

    @Test
    public void periodoValidoDelegaParaODao() {
        LocalDate ini = LocalDate.of(2026, 5, 1);
        LocalDate fim = LocalDate.of(2026, 5, 31);
        List<CompromissoVo> esperado = new ArrayList<>();
        esperado.add(new CompromissoVo());
        when(dao.findPorPeriodo(ini, fim)).thenReturn(esperado);

        assertSame(esperado, business.filtrarPorPeriodo(ini, fim));
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