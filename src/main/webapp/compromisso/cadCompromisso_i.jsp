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
			<s:form action="%{compromissoVo.rowid == null ? 'criarCompromissos' : 'atualizarCompromissos'}">
				<s:actionerror cssClass="alert alert-danger m-3"/>
				<s:fielderror cssClass="alert alert-danger m-3"/>

				<div class="card mt-5">
					<div class="card-header">
						<div class="row">
							<div class="col-sm-5">
								<s:url action="todosCompromissos" var="todos"/>
								<a href="${todos}" class="btn btn-success" >Compromissos</a>
							</div>
							
							<div class="col-sm">
								<h5 class="card-title">
									<s:if test="compromissoVo.rowid == null || compromissoVo.rowid == ''">
								    	Novo Compromisso
									</s:if>
								    <s:else>
								        Editar Compromisso
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
								<s:textfield cssClass="form-control" id="id" name="compromissoVo.rowid" readonly="true"/>							
							</div>	
						</div>
						
						<s:select
							cssClass="form-select"
							id="funcionario"
						    name="compromissoVo.funcionario.rowid"
						    list="listaFuncionarios"
							listKey="%{rowid}"
							listValueKey="%{nome}"
						    headerKey=""
							headerValue="Escolha..." 
						/>

						<s:select
							cssClass="form-select"
							id="agenda"
							name="compromissoVo.agenda.rowid"
							list="listaAgendas"
							listKey="%{rowid}"
							listValue="%{nome + ' (' + descricaoPeriodo + ')'}"
							headerKey=""
							headerValue="Escolha..." 
						/>

						<div class="col-sm-2">
							<s:textfield type="date" cssClass="form-control" id="data" name="compromissoVo.data"/>
						</div>
						
						<div class="col-sm-2">
							<s:textfield type="time" cssClass="form-control" id="hora" name="compromissoVo.hora"/>
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
			    document.getElementById('funcionario').selectedIndex = 0;
			    document.getElementById('agenda').selectedIndex = 0;
			    document.getElementById('data').value = '';
			    document.getElementById('hora').value = '';
			}
		</script>
	</body>
</html>