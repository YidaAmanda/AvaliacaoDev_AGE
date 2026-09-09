package br.com.soc.sistema.business;

import java.time.LocalDate;
import java.util.List;

import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioBusiness {
	public static final String DATAS_OBRIGATORIAS = "Informe a data inicial e a data final";
    public static final String DATA_INICIAL_POSTERIOR_A_FINAL = "A data inicial nao pode ser depois da data final";

    private CompromissoDao dao;

    public RelatorioBusiness() {
        this(new CompromissoDao());
    }

    RelatorioBusiness(CompromissoDao dao) {
        this.dao = dao;
    }

	public List<CompromissoVo> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null)
			throw new BusinessException(DATAS_OBRIGATORIAS);

		if (inicio.isAfter(fim))
			throw new BusinessException(DATA_INICIAL_POSTERIOR_A_FINAL);

		return dao.findPorPeriodo(inicio, fim);
	}
}