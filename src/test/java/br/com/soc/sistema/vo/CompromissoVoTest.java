package br.com.soc.sistema.vo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.time.LocalDate;
import org.junit.Test;

public class CompromissoVoTest {

    @Test
    public void dataNulaFormataComoStringVazia() {
        assertEquals("", new CompromissoVo().getDataFormatada());
    }

    @Test
    public void dataPreenchidaFormataDdMmYyyy() {
        CompromissoVo vo = new CompromissoVo();
        vo.setData(LocalDate.of(2026, 3, 9));
        assertEquals("09/03/2026", vo.getDataFormatada());
    }

    @Test
    public void toStringComFuncionarioEAgendaNulosNaoLancaNpe() {
        String texto = new CompromissoVo().toString();
        assertTrue(texto.contains("funcionario=null"));
        assertTrue(texto.contains("agenda=null"));
    }
}