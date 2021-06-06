package main;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class BBDDAccess {

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	final private String host = "localhost:3306";
	final private String user = "mobifirma";
	final private String password = "ST22-MobiFirma";

	public void generateLog() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");

		// Setup the connection with the DB
		connect = DriverManager
				.getConnection("jdbc:mysql://" + host + "/mobifirma?" + "user=" + user + "&password=" + password);

		// Statements allow to issue SQL queries to the database
		statement = connect.createStatement();
		// Result set get the result of the SQL query
		resultSet = statement.executeQuery(
				"SELECT * FROM mobifirma.pedidos\r\n" + "WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 0 MONTH)\r\n"
						+ "AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 0 MONTH)");
		double tact = calculateTrend(resultSet);

		resultSet = statement.executeQuery(
				"SELECT * FROM mobifirma.pedidos\r\n" + "WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)\r\n"
						+ "AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)");
		double tm1 = calculateTrend(resultSet);

		resultSet = statement.executeQuery(
				"SELECT * FROM mobifirma.pedidos\r\n" + "WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 2 MONTH)\r\n"
						+ "AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 2 MONTH)");

		double tm2 = calculateTrend(resultSet);
		System.out.println("Actual: " + tact);
		System.out.println("-1: " + tm1);
		System.out.println("-2: " + tm2);

		String tendencia = "";

		if ((tact > tm1 && tact > tm2) || (tact > tm1 && tact == tm2) || (tact == tm1 && tact > tm2)) {
			tendencia = "POSITIVA";
		} else if ((tact < tm1) || (tact < tm2)) {
			tendencia = "NEGATIVA";
		} else {
			tendencia = "NULA";
		}

		resultSet = statement.executeQuery("SELECT * FROM mobifirma.pedidos ORDER BY id DESC LIMIT 0, 1");

		while (resultSet.next()) {
			Integer id = resultSet.getInt("ID");
			String usuario = resultSet.getString("usuario");
			String camas = resultSet.getString("camas");
			String sillas = resultSet.getString("sillas");
			Date date = resultSet.getDate("fecha");
			String sillones = resultSet.getString("sillones");
			String mesas = resultSet.getString("mesas");
			Boolean accepted = resultSet.getBoolean("accepted");
			PrintWriter writer = new PrintWriter("LOG - P" + id + " - " + date + ".txt", "UTF-8");

			writer.println("///////PEDIDO NUMERO " + id + "///////////");
			writer.println("TENDENCIA ACTUAL: " + tendencia);
			writer.println("ULTIMO PEDIDO: ");
			writer.println("	Usuario: " + usuario);
			writer.println("	Fecha: " + date);
			writer.println("	Camas: " + camas);
			writer.println("	Sillas: " + sillas);
			writer.println("	Sillones: " + sillones);
			writer.println("	Mesas: " + mesas);
			writer.println("	Aprobado: " + accepted);
			writer.println("//////////");
			writer.close();
		}
	}

	private double calculateTrend(ResultSet resultSet) throws SQLException {
		Double aceptados = 0.0;
		Double totales = 0.0;
		while (resultSet.next()) {
			Boolean accepted = resultSet.getBoolean("accepted");
			if (accepted) {
				aceptados = aceptados + 1.0;
			}
			totales = totales + 1.0;

		}
		return aceptados / totales;
	}

	public boolean checkConnectionLimit() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");

		connect = DriverManager
				.getConnection("jdbc:mysql://" + host + "/mobifirma?" + "user=" + user + "&password=" + password);

		statement = connect.createStatement();
		resultSet = statement
				.executeQuery("SELECT * FROM mobifirma.pedidos\r\n" + "WHERE hora >= DATE_SUB(NOW(), INTERVAL 4 HOUR)");

		int counter = 0;
		while (resultSet.next()) {
			counter++;
		}

		if (counter >= 3) {
			return true;
		} else {
			return false;
		}
	}

	public void insertOrder(int mesas, int sillas, int sillones, int camas, int usuario, boolean verified)
			throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

			connect = DriverManager
					.getConnection("jdbc:mysql://" + host + "/mobifirma?" + "user=" + user + "&password=" + password);

			preparedStatement = connect
					.prepareStatement("insert into  mobifirma.pedidos values (default, ?, ?, ?, ? , ?, ?, ?, ?)");

			preparedStatement.setInt(1, usuario);
			preparedStatement.setInt(2, mesas);
			preparedStatement.setInt(3, sillas);
			preparedStatement.setInt(4, camas);
			preparedStatement.setInt(5, sillones);
			preparedStatement.setDate(6, sqlDate);
			preparedStatement.setTimestamp(7, date);
			preparedStatement.setBoolean(8, verified);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}