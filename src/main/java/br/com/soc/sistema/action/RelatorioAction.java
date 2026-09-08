package br.com.soc.sistema.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.soc.sistema.business.RelatorioBusiness;
import br.com.soc.sistema.business.RelatorioExporter;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioAction extends Action {
	public static final String NENHUM_COMPROMISSO_NO_PERIODO = "Nenhum compromisso encontrado no periodo informado";
	public static final String SEM_DADOS_PARA_EXPORTAR = "Nao ha dados para exportar no periodo informado";
	public static final String FALHA_GERAR_EXCEL = "Falha ao gerar o arquivo Excel";
	
	private RelatorioBusiness business = new RelatorioBusiness();
	private RelatorioExporter exporter = new RelatorioExporter();

	private LocalDate dataInicial;
	private LocalDate dataFinal;
	private List<CompromissoVo> compromissos = new ArrayList<>();
	private InputStream arquivo;

	public String abrir() {
		return SUCCESS;
	}

	public String gerar() {
		try {
			compromissos = business.filtrarPorPeriodo(dataInicial, dataFinal);

			if (compromissos.isEmpty())
				addActionMessage(NENHUM_COMPROMISSO_NO_PERIODO);
			} catch (BusinessException e) {
				addActionError(e.getMessage());
		}
		return SUCCESS;
    }

	public String exportar() {
    	try {
    		List<CompromissoVo> dados = business.filtrarPorPeriodo(dataInicial, dataFinal);

    		if (dados.isEmpty()) {
    			compromissos = dados;
    			addActionMessage(SEM_DADOS_PARA_EXPORTAR);
    			return INPUT;
    		}
    		
    		try (XSSFWorkbook wb = exporter.montarExcel(dados);
    			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    				wb.write(out);
    				arquivo = new ByteArrayInputStream(out.toByteArray());
    		}
    	} catch (BusinessException e) {
    		addActionError(e.getMessage());
    		return INPUT;
    	} catch (Exception e) {
    		addActionError(FALHA_GERAR_EXCEL);
    		return INPUT;
    	}
    	return "excel";
	}
	
	public InputStream getArquivo() {
		return arquivo;
	}

	public LocalDate getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(LocalDate dataInicial) {
		this.dataInicial = dataInicial;
	}

	public LocalDate getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(LocalDate dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<CompromissoVo> getCompromissos() {
		return compromissos;
	}
	public void setCompromissos(List<CompromissoVo> compromissos) {
		this.compromissos = compromissos;
	}
}