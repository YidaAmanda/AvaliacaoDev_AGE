package br.com.soc.sistema.infra;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.time.LocalTime;
import org.junit.Test;

public class PeriodoDisponivelTest {

    @Test
    public void manhaAceitaHorarioDaManha() {
        assertTrue(PeriodoDisponivel.MANHA.contemHorario(LocalTime.of(8, 0)));
    }

    @Test
    public void manhaRejeitaHorarioDaTarde() {
        assertFalse(PeriodoDisponivel.MANHA.contemHorario(LocalTime.of(13, 0)));
    }

    @Test
    public void contemHorarioComHoraNulaRetornaFalse() {
        assertFalse(PeriodoDisponivel.AMBOS.contemHorario(null));
    }

    @Test
    public void buscarPorCodigoValidoEstaPresente() {
        assertTrue(PeriodoDisponivel.buscarPor(1).isPresent());
    }

    @Test
    public void buscarPorCodigoInexistenteRetornaVazio() {
        assertFalse(PeriodoDisponivel.buscarPor(99).isPresent());
    }
}