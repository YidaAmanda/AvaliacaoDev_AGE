package br.com.soc.sistema.soap;

import javax.jws.WebService;

import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.vo.FuncionarioVo;

@WebService(endpointInterface = "br.com.soc.sistema.soap.WebServiceFuncionarios" )
public class WebServiceFuncionariosImpl implements WebServiceFuncionarios {

	private FuncionarioBusiness business;
	
	public WebServiceFuncionariosImpl() {
		this.business = new FuncionarioBusiness();
	}
	
	@Override
	public String buscarFuncionario(String cod) {
	    try {
	    	Long codigo = Long.parseLong(cod);
	        FuncionarioVo vo = business.buscarFuncionarioPor(codigo);
	        if (vo == null)
	            return "Funcionario nao encontrado";
	        return vo.toString();
	    } catch (NumberFormatException e) {
	        return "Foi informado um caracter no lugar de um numero";
	    } catch (BusinessException e) {
	        return e.getMessage();
	    }
	}
}
