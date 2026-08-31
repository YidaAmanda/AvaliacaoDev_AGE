package br.com.soc.sistema.business;

import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.FuncionarioFilter;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioBusiness {
	private static final int NOME_TAMANHO_MAXIMO = 255;
	private static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	private FuncionarioDao dao;
	
	public FuncionarioBusiness() {
		this.dao = new FuncionarioDao();
	}
	
	public List<FuncionarioVo> trazerTodosOsFuncionarios(){
		return dao.findAllFuncionarios();
	}	
	
	private void validarENormalizar(FuncionarioVo funcionarioVo) {
		if (funcionarioVo.getNome() == null)
			throw new BusinessException("Nome nao pode ser nulo");
		
		String nome = funcionarioVo.getNome().trim();
				
		if(nome.isEmpty())
				throw new BusinessException("Nome nao pode ser em branco");
		
		if(nome.length() > NOME_TAMANHO_MAXIMO)
			throw new BusinessException("Nome deve ter no maximo " + NOME_TAMANHO_MAXIMO + " caracteres");
		
		funcionarioVo.setNome(nome);
	}
	
	public void salvarFuncionario(FuncionarioVo funcionarioVo) {
		validarENormalizar(funcionarioVo);
		
		try {
			dao.insertFuncionario(funcionarioVo);
		} catch (Exception e) {
			throw new BusinessException("Nao foi possivel realizar a inclusao do registro");
		}
		
	}	
	
	public void atualizarFuncionario(FuncionarioVo funcionarioVo) {
	    validarENormalizar(funcionarioVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateFuncionario(funcionarioVo);
	    } catch (Exception e) {
	        throw new BusinessException("Nao foi possivel realizar a edicao do registro");
	    }

	    if (linhas == 0)
	        throw new BusinessException("Funcionario nao encontrado para atualizacao");
	}
	
	public List<FuncionarioVo> filtrarFuncionarios(FuncionarioFilter filter){
		List<FuncionarioVo> funcionarios = new ArrayList<>();
		
		switch (filter.getOpcoesCombo()) {
			case ID:
				try {
					Integer codigo = Integer.parseInt(filter.getValorBusca());
					funcionarios.add(dao.findByCodigo(codigo));
				}catch (NumberFormatException e) {
					throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
				}
			break;

			case NOME:
				funcionarios.addAll(dao.findAllByNome(filter.getValorBusca()));
			break;
		}
		
		return funcionarios;
	}
	
	public FuncionarioVo buscarFuncionarioPor(String codigo) {
		try {
			Integer cod = Integer.parseInt(codigo);
			return dao.findByCodigo(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
	}
}
