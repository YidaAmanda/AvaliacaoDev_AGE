<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina.cadastro"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
	</head>
	<body class="bg-secondary">

		<div class="container">
			<s:form action="%{agendaVo.rowid == null ? 'criarAgendas' : 'atualizarAgendas'}">
				<s:actionerror cssClass="alert alert-danger m-3"/>

				<div class="card mt-5">
					<div class="card-header">
						<div class="row">
							<div class="col-sm-5">
								<s:url action="todosAgendas" var="todos"/>
								<a href="${todos}" class="btn btn-success" >Agendas</a>
							</div>
							
							<div class="col-sm">
								<h5 class="card-title">
									<s:if test="agendaVo.rowid == null || agendaVo.rowid == ''">
								    	Nova Agenda
									</s:if>
								    <s:else>
								        Editar Agenda
								    </s:else>
								</h5>
							</div>
						</div>
					</div>
					
					<div class="card-body">
						<div class="row align-items-center">
							<label for="id" class="col-sm-1 col-form-label text-center">
								Código:
							</label>	

							<div class="col-sm-2">
								<s:textfield cssClass="form-control" id="id" name="agendaVo.rowid" readonly="true"/>							
							</div>	
						</div>
						
						<div class="row align-items-center mt-3">
							<label for="nome" class="col-sm-1 col-form-label text-center">
								Nome:
							</label>	

							<div class="col-sm-5">
								<s:textfield cssClass="form-control" id="nome" name="agendaVo.nome"/>							
							</div>	
						</div>
						
						<div class="row align-items-center mt-3">
						    <label for="periodo" class="col-sm-1 col-form-label text-center">
						        Período:
						    </label>
						    <div class="col-sm-5">
						        <s:select
						            cssClass="form-select"
						            id="periodo"
						            name="agendaVo.periodoDisponivel"
						            list="listaPeriodos"
						            listKey="%{codigo}"
						            listValueKey="%{descricao}"
						            headerKey=""
						            headerValue="Escolha..." />
						    </div>
						</div>
					</div>

					<div class="card-footer">
						<div class="form-row">
							<button class="btn btn-primary col-sm-4 offset-sm-1">Salvar</button>
							<button type="button" class="btn btn-secondary col-sm-4 offset-sm-2"
							        onclick="limparFormulario()">Limpar Formulario</button>
						</div>
					</div>
				</div>
			</s:form>			
		</div>
		
		<script src="webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
		
		<script>
			function limparFormulario() {
			    document.getElementById('nome').value = '';
			    document.getElementById('periodo').selectedIndex = 0;
			}
		</script>
	</body>
</html>