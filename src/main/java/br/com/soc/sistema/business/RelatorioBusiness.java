package br.com.soc.sistema.business;

import java.time.LocalDate;
import java.util.List;

import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioBusiness {

	private CompromissoDao dao = new CompromissoDao();

	public List<CompromissoVo> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null)
			throw new BusinessException("Informe a data inicial e a data final");

		if (inicio.isAfter(fim))
			throw new BusinessException("A data inicial nao pode ser depois da data final");

		return dao.findPorPeriodo(inicio, fim);
	}
}