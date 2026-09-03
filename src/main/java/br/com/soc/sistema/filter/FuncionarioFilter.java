package br.com.soc.sistema.filter;

public class FuncionarioFilter {

    public enum Criterio {
        TODOS("Todos"),
        CODIGO("Código"),
        NOME("Nome");
    	
        private final String descricao;
        
        Criterio(String descricao) {
        	this.descricao = descricao;
        }
        
        public String getCodigo() {
        	return name();
        }
        
        public String getDescricao() {
        	return descricao;
        }
    }

    private Criterio criterio;
    private String busca;

    public Criterio getCriterio() {
    	return criterio;
    }
    
    public void setCriterio(Criterio criterio) {
    	this.criterio = criterio;
    }

    public String getBusca() {
    	return busca;
    }
    
    public void setBusca(String busca) {
    	this.busca = busca;
    }

    public boolean semCriterioDeBusca() {
        if (criterio == null) return true;
        if (criterio == Criterio.TODOS) return false;
        
        return busca == null || busca.trim().isEmpty();
    }
}