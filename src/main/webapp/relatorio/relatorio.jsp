<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina.relatorio"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
		<style>
			@media print {
				.no-print { display: none !important; }
				.container { width: 100% !important; max-width: 100% !important; }
				body { background: #fff !important; }
			}
		</style>
	</head>
	<body class="bg-secondary">
		<div class="container">
			<div class="row mt-4 no-print">
			    <div class="col-sm p-0">
			        <div class="btn-group w-100" role="group">
			            <s:url action="todosFuncionarios" var="navFunc"/>
			            <a href="${navFunc}" class="btn btn-primary">Funcionários</a>

			            <s:url action="todosAgendas" var="navAgen"/>
			            <a href="${navAgen}" class="btn btn-primary">Agendas</a>

			            <s:url action="todosCompromissos" var="navComp"/>
			            <a href="${navComp}" class="btn btn-primary">Compromissos</a>
			        </div>
			    </div>
			</div>
			
			<div class="row mt-5 mb-2 no-print">
				<div class="col-sm p-0">
					<s:actionerror cssClass="alert alert-danger"/>
					<s:actionmessage cssClass="alert alert-warning"/>

					<s:form action="gerarRelatorios" method="post">
						<div class="input-group">
							<span class="input-group-text"><strong><s:text name="label.data.inicial"/></strong></span>
							<s:textfield type="date" cssClass="form-control" name="dataInicial" required="true"/>

							<span class="input-group-text"><strong><s:text name="label.data.final"/></strong></span>
							<s:textfield type="date" cssClass="form-control" name="dataFinal" required="true"/>

							<button class="btn btn-primary" type="submit"><s:text name="label.gerar"/></button>
							<button class="btn btn-success" type="submit" formaction="exportarRelatorios.action"><s:text name="label.exportar"/></button>
							<button class="btn btn-outline-dark" type="button" onclick="window.print()"><s:text name="label.imprimir"/></button>
						</div>
					</s:form>
				</div>
			</div>
			
			<div class="d-none d-print-block mb-3">
				<p class="mb-0">Período: <s:property value="dataInicialFormatada"/> a <s:property value="dataFinalFormatada"/></p>
			</div>

			<div class="row">
				<table class="table table-light table-striped align-middle">
					<thead>
						<tr>
							<th><s:text name="label.cod.compromisso"/></th>
							<th><s:text name="label.cod.funcionario"/></th>
							<th><s:text name="label.nome.funcionario"/></th>
							<th><s:text name="label.cod.agenda"/></th>
							<th><s:text name="label.nome.agenda"/></th>
							<th><s:text name="label.nome.periodo"/></th>
							<th><s:text name="label.data"/></th>
							<th><s:text name="label.hora"/></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="compromissos">
							<tr>
								<td>${rowid}</td>
								<td>${funcionario.rowid}</td>
								<td><s:property value="funcionario.nome"/></td>
								<td>${agenda.rowid}</td>
								<td><s:property value="agenda.nome"/></td>
								<td>${agenda.descricaoPeriodo}</td>
								<td>${dataFormatada}</td>
								<td>${horaFormatada}</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</div>

		<script src="webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
	</body>
</html>