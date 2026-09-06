package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class CompromissoDao extends Dao {
	
	private CompromissoVo montarCompromisso(ResultSet rs) throws SQLException {
	    AgendaVo agendaVo = new AgendaVo();
	    agendaVo.setRowid(rs.getLong("idAgen"));
	    agendaVo.setNome(rs.getString("nmAgen"));
	    agendaVo.setPeriodoDisponivel(rs.getInt("prdAgen"));

	    FuncionarioVo funcionarioVo = new FuncionarioVo();
	    funcionarioVo.setRowid(rs.getLong("idFunc"));
	    funcionarioVo.setNome(rs.getString("nmFunc"));

	    CompromissoVo vo = new CompromissoVo();
	    vo.setRowid(rs.getLong("idComp"));
	    vo.setAgenda(agendaVo);
	    vo.setFuncionario(funcionarioVo);
	    vo.setData(rs.getDate("dtComp").toLocalDate());
	    vo.setHora(rs.getTime("hrComp").toLocalTime());
	    return vo;
	}
	
	public boolean existeConflito(Long funcionarioId, LocalDate data, LocalTime hora, Long ignorarRowid) {
	    StringBuilder query = new StringBuilder("SELECT COUNT(*) total FROM compromisso ")
	    								.append("WHERE rowid_funcionario = ? AND dt_compromisso = ? AND hr_compromisso = ? ")
	    								.append("AND rowid <> ?");
	    try (Connection con = getConexao();
	         PreparedStatement ps = con.prepareStatement(query.toString())) {
	        int i = 1;
	        ps.setLong(i++, funcionarioId);
	        ps.setDate(i++, Date.valueOf(data));
	        ps.setTime(i++, Time.valueOf(hora));
	        ps.setLong(i++, ignorarRowid == null ? -1L : ignorarRowid);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() && rs.getInt("total") > 0;
	        }
	    } catch (SQLException e) {
	        throw new TechnicalException("Falha ao verificar conflito de compromisso", e);
	    }
	}
	
	public boolean existePorAgenda(Long codigo) {
	    StringBuilder query = new StringBuilder("SELECT COUNT(*) total FROM compromisso WHERE rowid_agenda = ? ");
	    try (Connection con = getConexao();
	         PreparedStatement ps = con.prepareStatement(query.toString())) {
	        int i = 1;
	        ps.setLong(i++, codigo);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() && rs.getInt("total") > 0;
	        }
	    } catch (SQLException e) {
	        throw new TechnicalException("Falha ao verificar se ha compromissos na agenda", e);
	    }
	}
	
	public void deleteCompromisso(Long codigo){
		StringBuilder query = new StringBuilder("DELETE FROM compromisso WHERE rowid=? ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i, codigo);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new TechnicalException("Falha ao excluir compromisso", e);
		}
	}
	
	public int updateCompromisso(CompromissoVo compromissoVo){
		StringBuilder query = new StringBuilder("UPDATE compromisso SET rowid_funcionario = ?, rowid_agenda = ?, ")
										.append("dt_compromisso = ?, hr_compromisso = ? WHERE rowid=? ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i++, compromissoVo.getFuncionario().getRowid());
			ps.setLong(i++, compromissoVo.getAgenda().getRowid());
			ps.setDate(i++, Date.valueOf(compromissoVo.getData()));
			ps.setTime(i++, Time.valueOf(compromissoVo.getHora()));
			ps.setLong(i++, compromissoVo.getRowid());
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new TechnicalException("Falha ao editar compromisso", e);
		}
	}
	
	public void insertCompromisso(CompromissoVo compromissoVo){
		StringBuilder query = new StringBuilder("INSERT INTO compromisso (rowid_funcionario, rowid_agenda, dt_compromisso, hr_compromisso) values (?, ?, ?, ?) ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i++, compromissoVo.getFuncionario().getRowid());
			ps.setLong(i++, compromissoVo.getAgenda().getRowid());
			ps.setDate(i++, Date.valueOf(compromissoVo.getData()));
			ps.setTime(i++, Time.valueOf(compromissoVo.getHora()));
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Falha ao inserir compromisso", e);
		}
	}

	
	public List<CompromissoVo> findAllCompromissos(){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ");
		try(Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){

			CompromissoVo compromissoVo =  null;
			List<CompromissoVo> compromissos = new ArrayList<>();
			while (rs.next()) {
				compromissoVo = new CompromissoVo();
				compromissoVo = montarCompromisso(rs);
				
				compromissos.add(compromissoVo);
			}
			return compromissos;
		} catch (SQLException e) {
		    throw new TechnicalException("Falha ao consultar compromissos", e);
		}
	}
	
	public CompromissoVo findByCodigo(Long codigo){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE c.rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setLong(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				CompromissoVo compromissoVo =  null;
				
				if (rs.next()) {
					compromissoVo = new CompromissoVo();
					compromissoVo = montarCompromisso(rs);
				}
				return compromissoVo;
			}
		} catch (SQLException e) {
		    throw new TechnicalException("Falha ao consultar compromissos", e);
		}
	}
	
	public List<CompromissoVo> findPorPeriodo(LocalDate inicio, LocalDate fim){
	    StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
	    								.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
	    								.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
	    								.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
	    								.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
	    								.append("WHERE c.dt_compromisso BETWEEN ? AND ? ")
	    								.append("ORDER BY c.dt_compromisso, c.hr_compromisso");

	    try(Connection con = getConexao();
	            PreparedStatement ps = con.prepareStatement(query.toString())){
	            int i = 1;

	            ps.setDate(i++, Date.valueOf(inicio));
	            ps.setDate(i++, Date.valueOf(fim));

	            try(ResultSet rs = ps.executeQuery()){
	                List<CompromissoVo> compromissos = new ArrayList<>();

	                while (rs.next()) {
	                    compromissos.add(montarCompromisso(rs));
	                }
	                return compromissos;
	            }
	        } catch (SQLException e) {
	            throw new TechnicalException("Falha ao consultar compromissos por periodo", e);
	        }
	}
	
	public List<CompromissoVo> findAllByAgenda(Long codigo){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE c.rowid_agenda = ?");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setLong(i, codigo);
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
	
	public List<CompromissoVo> findAllByNomeAgenda(String nome){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE lower(a.nm_agenda) like lower(?)");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setString(i, "%" + nome + "%");
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
	
	public List<CompromissoVo> findAllByFuncionario(Long codigo){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE c.rowid_funcionario = ?");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setLong(i, codigo);
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
	
	public List<CompromissoVo> findAllByNomeFuncionario(String nome){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE lower(f.nm_funcionario) like lower(?)");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setString(i, "%" + nome + "%");
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
	
	public List<CompromissoVo> findAllByPeriodo(Integer periodo){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE a.prd_disponivel = ?");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setInt(i, periodo);
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
	
	public List<CompromissoVo> findAllByData(LocalDate data){
		StringBuilder query = new StringBuilder("SELECT f.rowid idFunc, f.nm_funcionario nmFunc, ")
										.append("a.rowid idAgen, a.nm_agenda nmAgen, a.prd_disponivel prdAgen, ")
										.append("c.rowid idComp, c.dt_compromisso dtComp, c.hr_compromisso hrComp FROM compromisso c ")
										.append("INNER JOIN funcionario f ON f.rowid = c.rowid_funcionario ")
										.append("INNER JOIN agenda a ON a.rowid = c.rowid_agenda ")
										.append("WHERE c.dt_compromisso = ?");
		
		try(Connection con = getConexao();
				PreparedStatement ps = con.prepareStatement(query.toString())){
				int i = 1;
				
				ps.setDate(i, Date.valueOf(data));
				
				try(ResultSet rs = ps.executeQuery()){
					List<CompromissoVo> compromissos = new ArrayList<>();
					CompromissoVo compromissoVo =  null;
					
					while (rs.next()) {
						compromissoVo = new CompromissoVo();
						compromissoVo = montarCompromisso(rs);
						
						compromissos.add(compromissoVo);
					}
					return compromissos;
				}
			} catch (SQLException e) {
			    throw new TechnicalException("Falha ao consultar compromissos", e);
			}
	}
}