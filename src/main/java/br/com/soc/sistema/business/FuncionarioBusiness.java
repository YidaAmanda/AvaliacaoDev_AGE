package br.com.soc.sistema.business;

import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.FuncionarioFilter;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioBusiness {
	private static final int NOME_TAMANHO_MAXIMO = 255;
	public static final String NOME_EXCEDEU_LIMITE = "Nome deve ter no maximo " + NOME_TAMANHO_MAXIMO + " caracteres";
	
	public static final String FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO = "Foi informado um caracter no lugar de um numero";
	public static final String NOME_OBRIGATORIO = "Nome obrigatorio";
	public static final String NOME_EM_BRANCO = "Nome nao pode ser em branco";
	public static final String FALHA_INCLUSAO = "Nao foi possivel realizar a inclusao do registro";
	public static final String FALHA_EDICAO = "Nao foi possivel realizar a edicao do registro";
	public static final String FUNCIONARIO_NAO_ENCONTRADO_PARA_ATUALIZACAO = "Funcionario nao encontrado para atualizacao";
	public static final String FALHA_EXCLUSAO = "Erro ao excluir funcionario";
	
	private FuncionarioDao dao;
	
	public FuncionarioBusiness() {
		this.dao = new FuncionarioDao();
	}
	
	/*1*/
	public List<FuncionarioVo> trazerTodosOsFuncionarios(){
		return dao.findAllFuncionarios();
	}	
	
	public FuncionarioVo buscarFuncionarioPor(Long codigo) {
		return dao.findByCodigo(codigo);
	}
	
	public List<FuncionarioVo> filtrarFuncionarios(FuncionarioFilter filter){
		List<FuncionarioVo> funcionarios = new ArrayList<>();

	    switch (filter.getCriterio()) {
	        case TODOS:
	            funcionarios.addAll(dao.findAllFuncionarios());
	            break;

	        case CODIGO:
	            try {
	                Long codigo = Long.parseLong(filter.getBusca().trim());
	                FuncionarioVo vo = dao.findByCodigo(codigo);
	                if (vo != null) funcionarios.add(vo);
	            } catch (NumberFormatException e) {
	                throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	            }
	            break;

	        case NOME:
	            funcionarios.addAll(dao.findAllByNome(filter.getBusca().trim()));
	            break;
		}
		
		return funcionarios;
	}
	/*1*/

	/*2 - 3*/
	private void validarENormalizar(FuncionarioVo funcionarioVo) {
		if (funcionarioVo.getNome() == null)
			throw new BusinessException(NOME_OBRIGATORIO);
		
		String nome = funcionarioVo.getNome().trim();
				
		if(nome.isEmpty())
				throw new BusinessException(NOME_EM_BRANCO);
		
		if(nome.length() > NOME_TAMANHO_MAXIMO)
			throw new BusinessException(NOME_EXCEDEU_LIMITE);
		
		funcionarioVo.setNome(nome);
	}

	public void salvarFuncionario(FuncionarioVo funcionarioVo) {
		validarENormalizar(funcionarioVo);
		
		try {
			dao.insertFuncionario(funcionarioVo);
		} catch (Exception e) {
			throw new BusinessException(FALHA_INCLUSAO);
		}
		
	}

	public void atualizarFuncionario(FuncionarioVo funcionarioVo) {
	    validarENormalizar(funcionarioVo);

	    int linhas;
	    
	    try {
	        linhas = dao.updateFuncionario(funcionarioVo);
	    } catch (Exception e) {
	        throw new BusinessException(FALHA_EDICAO);
	    }

	    if (linhas == 0)
	        throw new BusinessException(FUNCIONARIO_NAO_ENCONTRADO_PARA_ATUALIZACAO);
	}
	/*2 - 3*/
	
	/*4*/
	public void excluirFuncionario(Long codigo) {
		try {
			dao.deleteFuncionario(codigo);
		}catch (Exception e) {
			throw new BusinessException(FALHA_EXCLUSAO);
		}
	}
	/*4*/
}
