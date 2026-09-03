package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.AgendaVo;

public class AgendaDao extends Dao {
	
	public void deleteAgenda(Long codigo){
		StringBuilder query = new StringBuilder("DELETE FROM agenda WHERE rowid=? ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i, codigo);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new TechnicalException("Falha ao excluir agenda", e);
		}
	}
	
	public int updateAgenda(AgendaVo agendaVo){
		StringBuilder query = new StringBuilder("UPDATE agenda SET nm_agenda = ?, prd_disponivel = ? WHERE rowid=? ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, agendaVo.getNome());
			ps.setInt(i++, agendaVo.getPeriodoDisponivel());
			ps.setLong(i++, agendaVo.getRowid());
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new TechnicalException("Falha ao editar agenda", e);
		}
	}
	
	public void insertAgenda(AgendaVo agendaVo){
		StringBuilder query = new StringBuilder("INSERT INTO agenda (nm_agenda, prd_disponivel) values (?, ?) ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, agendaVo.getNome());
			ps.setInt(i++, agendaVo.getPeriodoDisponivel());
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Falha ao inserir agenda", e);
		}
	}
	
	public List<AgendaVo> findAllAgendas(){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_agenda nome, prd_disponivel periodo FROM agenda ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){
			
			AgendaVo vo =  null;
			List<AgendaVo> agendas = new ArrayList<>();
			while (rs.next()) {
				vo = new AgendaVo();
				vo.setRowid(rs.getLong("id"));
				vo.setNome(rs.getString("nome"));	
				vo.setPeriodoDisponivel(rs.getInt("periodo"));
				agendas.add(vo);
			}
			return agendas;
		} catch (SQLException e) {
		    throw new TechnicalException("Falha ao consultar agendas", e);
		}
	}
	
	public List<AgendaVo> findAllByNome(String nome){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_agenda nome, prd_disponivel periodo FROM agenda ")
								.append("WHERE lower(nm_agenda) like lower(?)");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setString(i, "%"+nome+"%");
			
			try(ResultSet rs = ps.executeQuery()){
				AgendaVo vo =  null;
				List<AgendaVo> agendas = new ArrayList<>();
				
				while (rs.next()) {
					vo = new AgendaVo();
					vo.setRowid(rs.getLong("id"));
					vo.setNome(rs.getString("nome"));	
					vo.setPeriodoDisponivel(rs.getInt("periodo"));
					agendas.add(vo);
				}
				return agendas;
			}
		} catch (SQLException e) {
		    throw new TechnicalException("Falha ao consultar agendas", e);
		}
	}
	
	public List<AgendaVo> findAllByPeriodo(Integer periodo) {
	    StringBuilder query = new StringBuilder("SELECT rowid id, nm_agenda nome, prd_disponivel periodo FROM agenda ")
	                            .append("WHERE prd_disponivel = ?");
	    try (Connection con = getConexao();
	         PreparedStatement ps = con.prepareStatement(query.toString())) {
	        ps.setInt(1, periodo);
	        try (ResultSet rs = ps.executeQuery()) {
	            List<AgendaVo> agendas = new ArrayList<>();
	            while (rs.next()) {
	                AgendaVo vo = new AgendaVo();
	                vo.setRowid(rs.getLong("id"));
	                vo.setNome(rs.getString("nome"));
	                vo.setPeriodoDisponivel(rs.getInt("periodo"));
	                agendas.add(vo);
	            }
	            return agendas;
	        }
	    } catch (SQLException e) {
	        throw new TechnicalException("Falha ao consultar agendas", e);
	    }
	}
	
	public AgendaVo findByCodigo(Long codigo){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_agenda nome, prd_disponivel periodo FROM agenda ")
								.append("WHERE rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setLong(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				AgendaVo vo =  null;
				
				if (rs.next()) {
					vo = new AgendaVo();
					vo.setRowid(rs.getLong("id"));
					vo.setNome(rs.getString("nome"));	
					vo.setPeriodoDisponivel(rs.getInt("periodo"));
				}
				return vo;
			}
		} catch (SQLException e) {
		    throw new TechnicalException("Falha ao consultar agendas", e);
		}
	}
}