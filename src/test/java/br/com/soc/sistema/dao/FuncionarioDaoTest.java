package br.com.soc.sistema.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.Test;

import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioDaoTest {

    private final FuncionarioDao funcionarioDao = new FuncionarioDao();
    private final CompromissoDao compromissoDao = new CompromissoDao();

    @Test
    public void excluirFuncionarioApagaSeusCompromissos() {
        String nome = "ZZ_Cascata_" + System.nanoTime();
        FuncionarioVo novo = new FuncionarioVo();
        novo.setNome(nome);
        funcionarioDao.insertFuncionario(novo);

        Long idFuncionario = funcionarioDao.findAllByNome(nome).get(0).getRowid();
        assertNotNull(idFuncionario);

        FuncionarioVo f = new FuncionarioVo(); f.setRowid(idFuncionario);
        AgendaVo a = new AgendaVo(); a.setRowid(1L);
        CompromissoVo compromisso = new CompromissoVo();
        compromisso.setFuncionario(f);
        compromisso.setAgenda(a);
        compromisso.setData(LocalDate.now().plusDays(7));
        compromisso.setHora(LocalTime.of(10, 0));
        compromissoDao.insertCompromisso(compromisso);

        List<CompromissoVo> antes = compromissoDao.findAllByFuncionario(idFuncionario);
        assertFalse("deveria haver compromisso antes da exclusao", antes.isEmpty());

        int linhasFuncionario = funcionarioDao.deleteFuncionario(idFuncionario);
        assertEquals(1, linhasFuncionario);

        assertNull(funcionarioDao.findByCodigo(idFuncionario));
        assertTrue("os compromissos deveriam ter sido apagados junto",
                   compromissoDao.findAllByFuncionario(idFuncionario).isEmpty());
    }

    @Test
    public void findByCodigoInexistenteRetornaNull() {
        assertNull(funcionarioDao.findByCodigo(999999L));
    }
}