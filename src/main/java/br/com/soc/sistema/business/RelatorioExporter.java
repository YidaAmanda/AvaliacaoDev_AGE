package br.com.soc.sistema.business;

import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.util.CellRangeAddress;

import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioExporter {

	public XSSFWorkbook montarExcel(List<CompromissoVo> compromissos, LocalDate inicio, LocalDate fim) {
		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet aba = wb.createSheet("Compromissos");

		String[] cabecalho = {"Cód. Compromisso", "Cód. Funcionario", "Funcionário", "Cód. Agenda", "Agenda", "Período", "Data", "Hora"};

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Row periodo = aba.createRow(0);
		periodo.createCell(0).setCellValue("Data inicial: " + inicio.format(dtf) + " | Data final: " + fim.format(dtf));
		aba.addMergedRegion(new CellRangeAddress(0, 0, 0, cabecalho.length - 1));

		Row titulo = aba.createRow(1);
	    for (int i = 0; i < cabecalho.length; i++)
	        titulo.createCell(i).setCellValue(cabecalho[i]);

	    int r = 2;
	    for (CompromissoVo vo : compromissos) {
	    	Row linha = aba.createRow(r++);
	    	int i = 0;
	    	linha.createCell(i++).setCellValue(vo.getRowid());
	    	linha.createCell(i++).setCellValue(vo.getFuncionario().getRowid());
	    	linha.createCell(i++).setCellValue(vo.getFuncionario().getNome());
	    	linha.createCell(i++).setCellValue(vo.getAgenda().getRowid());
	    	linha.createCell(i++).setCellValue(vo.getAgenda().getNome());
	    	linha.createCell(i++).setCellValue(vo.getAgenda().getDescricaoPeriodo());
	    	linha.createCell(i++).setCellValue(vo.getDataFormatada());
	    	linha.createCell(i++).setCellValue(vo.getHoraFormatada());
	    }

	    for (int i = 0; i < cabecalho.length; i++)
	    	aba.autoSizeColumn(i);

	    return wb;
	}
}