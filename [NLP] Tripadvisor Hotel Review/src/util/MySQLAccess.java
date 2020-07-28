package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import lexical.PorterStemmer;

public class MySQLAccess {
  private static Connection connect = null;
  private static Statement statement = null;
  private static PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;

  public void readDataBase() throws Exception {
    
	  try {
      //********************** Connessione al DB **********************
		  
	  // Caricamento dei driver del DB
      Class.forName("com.mysql.jdbc.Driver");
      // Connessione al server locale, db mediante user e psw
      connect = DriverManager.getConnection("jdbc:mysql://localhost/db_test_connesstione?" + "user=root&password=");

      // Statements allow to issue SQL queries to the database
      statement = connect.createStatement();
      // Result set get the result of the SQL query
      resultSet = statement.executeQuery("select * from db_test_connesstione.comments");
      writeResultSet(resultSet);

      // PreparedStatements can use variables and are more efficient
      preparedStatement = connect
          .prepareStatement("insert into  db_test_connesstione.comments values (default, ?, ?, ?, ? , ?, ?)");
      // "myuser, webpage, datum, summery, COMMENTS from db_test_connesstione.comments");
      // Parameters start with 1
      preparedStatement.setString(1, "Test");
      preparedStatement.setString(2, "TestEmail");
      preparedStatement.setString(3, "TestWebpage");
      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
      preparedStatement.setString(5, "TestSummary");
      preparedStatement.setString(6, "TestComment");
      preparedStatement.executeUpdate();

      
      preparedStatement = connect
          .prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from db_test_connesstione.comments");
      resultSet = preparedStatement.executeQuery();
      
      writeResultSet(resultSet);

      // Remove again the insert comment
      preparedStatement = connect
      .prepareStatement("delete from db_test_connesstione.comments where myuser= ? ; ");
      preparedStatement.setString(1, "Test");
      preparedStatement.executeUpdate();
      
      resultSet = statement
      .executeQuery("select * from db_test_connesstione.comments");
      writeMetaData(resultSet);
      
    } catch (Exception e) {
      throw e;
    } finally {
      close();
    }

  }
  
  public static void insert(String value) {
	   try {
		   preparedStatement = connect.prepareStatement("INSERT INTO db_tripadvisor.tb_stopwords (value) values (?)");
		   preparedStatement.setString(1, value);
		   preparedStatement.executeUpdate();
	   }
	   catch (SQLException e) {
			e.printStackTrace();}
	}
  
  public static int getTotalDictWords(String query) throws SQLException{
	  //openConnection();
	  try {
		  ResultSet resultSet = statement.executeQuery(query);
		  while (resultSet.next()) {
		  //close();
		  return resultSet.getInt(1);}
	   }
	   catch (SQLException e) {
			e.printStackTrace();}
	  
	  return 0;
  }
  
  public static int getIdDictWord(String query) throws SQLException{
	  try {
		  ResultSet resultSet = statement.executeQuery(query);
		  while (resultSet.next()) {
			  return resultSet.getInt(1);}
	   }
	   catch (SQLException e) {
			e.printStackTrace();}
	  
	  return 0;
  }
  
  public static String getValue(String query) throws SQLException{
	  try {
		  ResultSet resultSet = statement.executeQuery(query);
		  while (resultSet.next()) {
			  return resultSet.getString(1);}
	   }
	   catch (SQLException e) {
			e.printStackTrace();}
	  
	  return "error";
  }
  
  public static boolean finder(String query) throws SQLException{
	  try {
		  ResultSet resultSet = statement.executeQuery(query);
		  return resultSet.next();
		  }
	   catch (SQLException e) {
			e.printStackTrace();}
	  return false;
  }
  
  public static ResultSet getDistributionMatrix() throws SQLException{
	  try {
		  ResultSet resultSet = statement.executeQuery("SELECT ov1, ov2, ov3, ov4, ov5 FROM tb_distributions WHERE 1");
		  /*while (resultSet.next()){
			  System.out.println(resultSet.getString("ov1") + " " + resultSet.getString("ov2") + " " + resultSet.getString("ov3") + " " + resultSet.getString("ov4") + " " + resultSet.getString("ov5"));
		  }*/
		  return resultSet;
	   }
	   catch (SQLException e) {
			e.printStackTrace();}
	  
	  return null;
  }
  
  
  public static Map mapping(String query) throws SQLException{
	  
	  openConnection();
	  ResultSet resultSet = null;
	  Map<String, String> map = new HashMap<String, String>();
	  
	  try {
		  resultSet = statement.executeQuery(query);
		  while(resultSet.next()){
			  map.put(resultSet.getString(2), resultSet.getString(1));}
	  }
	  catch (SQLException e) {
	      e.printStackTrace();}
	  
	  close();
	  resultSet.close();
	  return map;
	  
  }//End public static Map mapping() throws SQLException
  
public static void tesa() throws SQLException{
	  
	  openConnection();
	  ResultSet resultSet = null;
	  
	  try {
		  resultSet = statement.executeQuery("SELECT word FROM lol WHERE 1");
		  while(resultSet.next()){
			  String str = resultSet.getString(1);
			  System.out.println("query: " + "INSERT INTO tb_dict_eng (word) VALUES (" + PorterStemmer.doStemming(str) + ")");
			  push("INSERT INTO tb_dict_eng (word) VALUES ('" + PorterStemmer.doStemming(str) + "')");
		  }
	  }
	  catch (SQLException e) {
	      e.printStackTrace();}
	  
	  resultSet.close();
	  close();
	  
  }//End public static Map mapping() throws SQLException
  
  
  /*public static boolean (String query) throws SQLException{
	  try {
		  ResultSet resultSet = statement.executeQuery(query);
		  return resultSet.next();
		  }
	   catch (SQLException e) {
			e.printStackTrace();}
	  return false;
  }*/
  
  
  public static void openConnection(){
	  try {
		// Connessione al server locale, db mediante user e psw
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/db_tripadvisor?" + "user=root&password=");
		statement = connect.createStatement();
	} catch (Exception e) {
		
		e.printStackTrace();
	}
  }
      
	public static ResultSet printData(String query){
		//ResultSet resultSet;
		try {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		/*try {
			return statement.executeQuery(query);
			/*while (resultSet.next()) {
			      // It is possible to get the columns via name
			      // also possible to get the columns via the column number
			      // which starts at 1
			      // e.g. resultSet.getSTring(2);
		        String name = resultSet.getString("name"); //
			    //Date date = resultSet.getDate("datum");
		        System.out.println("User: " + name);
		    }//End while (resultSet.next())
		}//End try
			catch (SQLException e) {
			e.printStackTrace();}      */
	}//End public static void printData(String query)
	
  
  public static void push (String query){ //Esegue una query di inserimento / modifica
	  try {
		  preparedStatement = connect.prepareStatement(query);
	      preparedStatement.executeUpdate();
	      preparedStatement.close();}
	  catch (SQLException e) {
		  e.printStackTrace();}
	  
  }//End public static void push (String query)
  
  
  /*public static void push (String query){ //Esegue una query di inserimento / modifica
	  try {
		  //openConnection();
		  PreparedStatement preparedStatement = connect.prepareStatement(query);
	      preparedStatement.executeUpdate();}
	  catch (SQLException e) {
		  System.out.println("errore: " + query);}
	  
  }//End public static void push (String query)*/
  
	//statement = connect.createStatement();

  

  private void writeMetaData(ResultSet resultSet) throws SQLException {
    //   Now get some metadata from the database
    // Result set get the result of the SQL query
    
    System.out.println("The columns in the table are: ");
    
    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
    }
  }

  private void writeResultSet(ResultSet resultSet) throws SQLException {
    // ResultSet is initially before the first data set
    while (resultSet.next()) {
      // It is possible to get the columns via name
      // also possible to get the columns via the column number
      // which starts at 1
      // e.g. resultSet.getSTring(2);
      String user = resultSet.getString("myuser");
      String website = resultSet.getString("webpage");
      String summery = resultSet.getString("summery");
      Date date = resultSet.getDate("datum");
      String comment = resultSet.getString("comments");
      
      System.out.println("User: " + user);
      System.out.println("Website: " + website);
      System.out.println("Summery: " + summery);
      System.out.println("Date: " + date);
      System.out.println("Comment: " + comment);
    }
  }
  

  // You need to close the resultSet
  public static void close() {
      try {
          if (statement != null) {
            statement.close(); }

          if (connect != null) {
            connect.close();}
      } 
      catch (Exception e) {}
  }

} 