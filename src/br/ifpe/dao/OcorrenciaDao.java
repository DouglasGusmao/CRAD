package br.ifpe.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.Connection;
import br.ifpe.basicas.Ocorrencia;
import br.ifpe.basicas.Solicitacao;
import br.ifpe.basicas.TipoSolicitacao;
import br.ifpe.basicas.Usuario;
import br.ifpe.util.ConnectionFactory;


public class OcorrenciaDao {


	private Connection connection;

	public OcorrenciaDao(){
		try {
			this.connection = (Connection) new ConnectionFactory().getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
//	registrar solicitacao
	public void registrar(Ocorrencia ocorrencia){

		try {
			
			String sql = "INSERT INTO solicitacao(acao,data_hora,fk_usuario,fk_solicitacao) VALUES (?,?,?,?)";
			
			PreparedStatement stmt = (PreparedStatement) connection.prepareStatement(sql);

			stmt.setString(1, ocorrencia.getAcao());
			stmt.setDate(2, new java.sql.Date(ocorrencia.getDataHora().getTime()));
			stmt.setInt(3, ocorrencia.getUsuario().getId());
			stmt.setInt(4, ocorrencia.getSolicitacao().getId());



			stmt.execute();
			connection.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//montar objeto
	private Ocorrencia montarObjeto(ResultSet rs) throws SQLException {

		Ocorrencia ocorrencia = new Ocorrencia();

		ocorrencia.setId(rs.getInt("id"));
		ocorrencia.setAcao(rs.getString("acao"));
		ocorrencia.setDataHora(rs.getDate("data_hora"));

		//		montando o objeto com a chave estrangeira
		UsuarioDao dao1 = new UsuarioDao();
		Usuario usuario = dao1.buscarPorId(rs.getInt("fk_usuario"));
		ocorrencia.setUsuario(usuario);

		//		montando o objeto com a chave estrangeira			
		SolicitacaoDao dao2 = new SolicitacaoDao();
		Solicitacao solicitacao = dao2.buscarPorId(rs.getInt("fk_solicitacao"));
		ocorrencia.setSolicitacao(solicitacao);
		
		return ocorrencia;
	}
	
	// listar Ocorrencia
	public List<Ocorrencia> listarOcorrencia() {

		try {

			List<Ocorrencia> ocorrencia = new ArrayList<Ocorrencia>();
			String sql = "SELECT * FROM ocorrencia ORDER BY descricao";
			PreparedStatement stmt = (PreparedStatement) connection.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ocorrencia.add(montarObjeto(rs));
			}

			rs.close();
			stmt.close();
			connection.close();

			return ocorrencia;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}	
	
	//buscar por id
	public Ocorrencia buscarPorId(int id) {

	try {

		PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM ocorrencia WHERE id = ?");
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();

		Ocorrencia ocorrencia = new Ocorrencia();

		if (rs.next()) {
			ocorrencia = montarObjeto(rs);
		}

		rs.close();
		stmt.close();
		connection.close();

		return ocorrencia;

	} catch (SQLException e) {
		throw new RuntimeException(e);
	}
}
}

