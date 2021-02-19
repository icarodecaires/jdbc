package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;
import db.DbException;
import db.DbIntegrityException;

public class Program {

	public static void main(String[] args) {

		//recuperandoDados();
		//inserindoDados();
		//atualizandoDados();
		//deletarDados();
		transacoes();
	}
	
	public static void transacoes() {
		Connection conn = null;
		Statement st = null;
		
		try {
			
			conn = DB.getConnection();
			
			conn.setAutoCommit(false);
			
			st = conn.createStatement();
			int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");
			
			int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");
			
			conn.commit();
			System.out.println("rows1: "+rows1);
			System.out.println("rows1: "+rows2);
			
			
		}
		catch(SQLException e) {
			try {
				conn.rollback();
				
				throw new DbException("Transação Não Concluida Causada por "+e.getMessage());
				
			} catch (SQLException e1) {
				throw new DbException("Erro ao Tentar Realizar o rollback "+e.getMessage());
			}
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
	
	public static void deletarDados() {
		Connection conn = null;
		PreparedStatement st = null;
		
		try {
			
			conn = DB.getConnection();
			
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			st.setInt(1, 2);
			
			int rowsAffected = st.executeUpdate();
			System.out.println("LINHAS AFETADAS: "+rowsAffected);
			
		}
		catch(SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
	
	public static void atualizandoDados() {
		Connection conn = null;
		PreparedStatement st = null;
		
		try {
			
			conn = DB.getConnection();
			
			st = conn.prepareStatement(
					"UPDATE seller SET BaseSalary = BaseSalary + ? "
					+"WHERE (DepartmentId = ?)");
			st.setDouble(1, 200);
			st.setInt(2, 2);
			
			int rowsAffected = st.executeUpdate();
			System.out.println("LINHAS AFETADAS: "+rowsAffected);
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
		
	}
	
	public static void inserindoDados() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Connection conn = null;
		PreparedStatement st = null;
		
		try {
			conn = DB.getConnection();
			
		
			//Inserindo apenas um elemento
			st = conn.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId)"
					+"VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, "Carl Purple");
			st.setString(2, "carl@gmail.com");
			st.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
			st.setDouble(4, 3000.00);
			st.setInt(5, 4);
			
			
		
			//Operação para alteração de dados
			int rowsAffected = st.executeUpdate();


			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Id: "+id);
				}
			}else {
				System.out.println("Nenhuma Linha Alterada");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		} catch (ParseException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
		
	}

	public static void recuperandoDados() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			conn = DB.getConnection();

			st = conn.createStatement();

			rs = st.executeQuery("select * from department");
			
			while (rs.next()) {
				System.out.println(rs.getInt("Id")+", "+rs.getString("name"));
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}

}
