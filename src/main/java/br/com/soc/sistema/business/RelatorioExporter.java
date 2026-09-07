package br.com.soc.sistema.business;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioExporter {

	public XSSFWorkbook montarExcel(List<CompromissoVo> compromissos) {
		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet aba = wb.createSheet("Compromissos");

		String[] cabecalho = {"Cod. Compromisso", "Cod. Funcionario", "Funcionario", "Cod. Agenda", "Agenda", "Periodo", "Data", "Hora"};
		Row titulo = aba.createRow(0);
		for (int i = 0; i < cabecalho.length; i++)
			titulo.createCell(i).setCellValue(cabecalho[i]);

		int r = 1;
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
		return wb;
	}
}