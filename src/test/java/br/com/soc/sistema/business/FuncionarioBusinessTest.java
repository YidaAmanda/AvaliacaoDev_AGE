package br.com.soc.sistema.business;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Test;

import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioBusinessTest {

    private final FuncionarioDao funcionarioDao = new FuncionarioDao();
    private final CompromissoDao compromissoDao = new CompromissoDao();
    private final FuncionarioBusiness business = new FuncionarioBusiness(funcionarioDao);

    @Test
    public void excluirFuncionarioViaBusinessApagaSeusCompromissos() {
        String nome = "Cascade_" + System.nanoTime();
        FuncionarioVo novo = new FuncionarioVo();
        novo.setNome(nome);
        funcionarioDao.insertFuncionario(novo);
        Long idFunc = funcionarioDao.findAllByNome(nome).get(0).getRowid();

        AgendaVo agenda = new AgendaVo(); agenda.setRowid(1L);
        CompromissoVo c = new CompromissoVo();
        FuncionarioVo f = new FuncionarioVo(); f.setRowid(idFunc);
        c.setFuncionario(f);
        c.setAgenda(agenda);
        c.setData(LocalDate.now().plusDays(7));
        c.setHora(LocalTime.of(10, 0));
        compromissoDao.insertCompromisso(c);

        assertFalse(compromissoDao.findAllByFuncionario(idFunc).isEmpty());

        business.excluirFuncionario(idFunc);

        assertNull("funcionário deveria ter sido apagado",
                   funcionarioDao.findByCodigo(idFunc));
        assertTrue("compromissos deveriam cair junto (cascade)",
                   compromissoDao.findAllByFuncionario(idFunc).isEmpty());
    }
}