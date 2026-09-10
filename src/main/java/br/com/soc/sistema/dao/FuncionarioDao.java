package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioDao extends Dao {
	private static final String FALHA_CONSULTAR = "Falha ao consultar funcionários";
	private static final String FALHA_INSERIR = "Falha ao inserir funcionário";
	private static final String FALHA_EDITAR = "Falha ao editar funcionário";
	private static final String FALHA_EXCLUIR = "Falha ao excluir funcionário e seus compromissos";
	
	/*1*/
	public List<FuncionarioVo> findAllFuncionarios(){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){
			
			FuncionarioVo vo =  null;
			List<FuncionarioVo> funcionarios = new ArrayList<>();
			while (rs.next()) {
				vo = new FuncionarioVo();
				vo.setRowid(rs.getLong("id"));
				vo.setNome(rs.getString("nome"));	
				
				funcionarios.add(vo);
			}
			return funcionarios;
		} catch (SQLException e) {
		    throw new TechnicalException(FALHA_CONSULTAR, e);
		}
	}
	
	public FuncionarioVo findByCodigo(Long codigo){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario ")
								.append("WHERE rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setLong(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				FuncionarioVo vo =  null;
				
				if (rs.next()) {
					vo = new FuncionarioVo();
					vo.setRowid(rs.getLong("id"));
					vo.setNome(rs.getString("nome"));	
				}
				return vo;
			}
		} catch (SQLException e) {
		    throw new TechnicalException(FALHA_CONSULTAR, e);
		}
	}
	
	public List<FuncionarioVo> findAllByNome(String nome){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario ")
										.append("WHERE ").append(semAcento("nm_funcionario"))
										.append(" LIKE ").append(semAcento("?")).append(" ESCAPE '\\'");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setString(i, "%" + escapeLike(nome) + "%");
			
			try(ResultSet rs = ps.executeQuery()){
				FuncionarioVo vo =  null;
				List<FuncionarioVo> funcionarios = new ArrayList<>();
				
				while (rs.next()) {
					vo = new FuncionarioVo();
					vo.setRowid(rs.getLong("id"));
					vo.setNome(rs.getString("nome"));	
					
					funcionarios.add(vo);
				}
				return funcionarios;
			}
		} catch (SQLException e) {
		    throw new TechnicalException(FALHA_CONSULTAR, e);
		}
	}
	/*1*/
	
	/*2*/
	public void insertFuncionario(FuncionarioVo funcionarioVo){
		StringBuilder query = new StringBuilder("INSERT INTO funcionario (nm_funcionario) values (?)");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, funcionarioVo.getNome());
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException(FALHA_INSERIR, e);
		}
	}
	/*2*/
	
	/*3*/
	public int updateFuncionario(FuncionarioVo funcionarioVo){
		StringBuilder query = new StringBuilder("UPDATE funcionario SET nm_funcionario = ? WHERE rowid=?");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, funcionarioVo.getNome());
			ps.setLong(i++, funcionarioVo.getRowid());
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new TechnicalException(FALHA_EDITAR, e);
		}
	}
	/*3*/
	
	/*4*/
	public int deleteFuncionario(Long codigo) {
	    String delComp = "DELETE FROM compromisso WHERE rowid_funcionario = ?";
	    String delFunc = "DELETE FROM funcionario WHERE rowid = ?";

	    Connection con = null;
	    try {
	        con = getConexao();
	        con.setAutoCommit(false);

	        int linhasFuncionario;
	        try (PreparedStatement psComp = con.prepareStatement(delComp);
	             PreparedStatement psFunc = con.prepareStatement(delFunc)) {

	            psComp.setLong(1, codigo);
	            psComp.executeUpdate();

	            psFunc.setLong(1, codigo);
	            linhasFuncionario = psFunc.executeUpdate();
	        }

	        con.commit();
	        return linhasFuncionario;
	    } catch (SQLException e) {
	        try {
	            if (con != null) con.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        throw new TechnicalException(FALHA_EXCLUIR, e);
	    } finally {
	        try {
	            if (con != null) {
	                con.setAutoCommit(true);
	                con.close();
	            }
	        } catch (SQLException closeEx) {
	            closeEx.printStackTrace();
	        }
	    }
	}
	/*4*/	
}