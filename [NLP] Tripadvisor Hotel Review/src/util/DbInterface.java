package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import core.Train;

public class DbInterface {
	
	public static void loadStopWords(){ //Carica le stopwords nel db
		String strLinea = "";
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader("StopwordCustom.txt"));
			strLinea=inputStream.readLine();
			MySQLAccess.openConnection();
			MySQLAccess.push("TRUNCATE tb_stopwords");
			while(strLinea!= null){
				MySQLAccess.push("INSERT INTO db_tripadvisor.tb_stopwords (value) VALUES ('" + strLinea + "');");
				strLinea=inputStream.readLine();}}
		catch (FileNotFoundException e) {
				e.printStackTrace();}
		catch (IOException e) {
		        e.printStackTrace();}
		MySQLAccess.close();
	}//End public static void loadStopWords()
	
	
	public static void loadIntoDictionary2(String text, int idReview, String hotelName){ //Salva in una tabella provvisoria ciascuna parola di una recensione
		StringTokenizer tokens = null;
		try {
			tokens = new StringTokenizer(text, ",	/ -\n\t():.;\'?\\#`&\"_+=$!*|~%");}
		catch (NullPointerException e) {
			e.printStackTrace();}
		catch (Exception e2) {}

		while (tokens.hasMoreElements()) {
			MySQLAccess.push("INSERT INTO db_tripadvisor.tb_dictionary_raw (value, idReview, hotelName) VALUES ('" + tokens.nextElement().toString() + "', " + idReview + ", '" + hotelName + "');");}
	}//End public static void loadIntoDictionary2(String text, int idReview, String hotelName)
	
	public static double [] getPriorProbability(){
		ResultSet resultSet = MySQLAccess.printData("SELECT ov1, ov2, ov3, ov4, ov5 FROM db_tripadvisor.tb_prior WHERE 1");
		double arrPrior[] = new double[5];
		
		try {
			while (resultSet.next()){
				for (int i=0; i<5; i++){
					arrPrior[i] = Double.parseDouble(resultSet.getString(i+1));}}
			resultSet.close();} 
		catch (SQLException e) {
			e.printStackTrace();}
		
		return arrPrior;

	}//End public static double [] getPriorProbability()
	
	public static void createDict(){ //Procedura di creazione del dizionario
		MySQLAccess.openConnection();
		MySQLAccess.push("INSERT INTO db_tripadvisor.tb_dictionary_tmp (value, idf) SELECT value, COUNT(value) as idf FROM (SELECT value FROM `tb_dictionary_raw` WHERE 1 GROUP BY idReview, hotelName, value) as sq WHERE value IN (SELECT word FROM tb_dict_eng) GROUP BY value");
		//MySQLAccess.push("INSERT INTO db_tripadvisor.tb_dictionary (value, idf) SELECT value, COUNT(value) as idf FROM (SELECT value FROM `tb_dictionary_raw` WHERE 1 GROUP BY idReview, hotelName, value) as sq WHERE value IN (SELECT word FROM entries) GROUP BY value");
		MySQLAccess.push("INSERT INTO db_tripadvisor.tb_dictionary (value, idf) SELECT value, idf FROM db_tripadvisor.tb_dictionary_tmp WHERE idf>=10");

		//MySQLAccess.push("DELETE FROM db_tripadvisor.tb_dictionary WHERE idf<=50");
		//Gadget.stop();
		MySQLAccess.push("INSERT INTO db_tripadvisor.tb_distributions (value) SELECT value FROM db_tripadvisor.tb_dictionary WHERE 1");
		MySQLAccess.close();
	}//End public static void createDict()
	
	
	public static void inizializeDB(){ //Inizializza il database svuotandolo 
		MySQLAccess.openConnection();
		MySQLAccess.push("TRUNCATE tb_dictionary");
		MySQLAccess.push("TRUNCATE tb_dictionary_tmp");
		MySQLAccess.push("TRUNCATE tb_distributions");
		MySQLAccess.push("TRUNCATE tb_dictionary_raw");
		MySQLAccess.push("TRUNCATE tb_result");
		MySQLAccess.push("TRUNCATE tb_prior");

		MySQLAccess.close();
	}//End public static void inizializeDB()
	
    public static void savePrior(int [] crossValidation, int totReview){
		MySQLAccess.push("INSERT INTO db_tripadvisor.tb_prior (ov1, ov2, ov3, ov4, ov5) VALUES (" + ((double)crossValidation[0]/totReview) + ", " + ((double)crossValidation[1]/totReview) + ", " + ((double)crossValidation[2]/totReview) + ", " + ((double)crossValidation[3]/totReview) + ", " + ((double)crossValidation[4]/totReview) + ")");

    }

	
	public static void saveMatrix(double matrix[][]){ //Salva la matrice di distribuzione
		for (int col=0; col<matrix[0].length; col++){
			Train.printWriterLog.println("UPDATE db_tripadvisor.tb_distributions SET ov1=" + matrix[0][col] + ", ov2=" + matrix[1][col] + ", ov3=" + matrix[2][col] + ", ov4=" + matrix[3][col] + ", ov5=" + matrix[4][col] + " WHERE ID=" + (col+1));
			MySQLAccess.push("UPDATE db_tripadvisor.tb_distributions SET ov1=" + matrix[0][col] + ", ov2=" + matrix[1][col] + ", ov3=" + matrix[2][col] + ", ov4=" + matrix[3][col] + ", ov5=" + matrix[4][col] + " WHERE ID=" + (col+1));}
	}//End public static void saveMatrix(double matrix[][])
	
	
	public static double [][] loadMatrixDistributions(int totWords){ //Restituisce la tabella delle distribuzioni sotto forma di matrice
		try {
			Train.printWriterLog =  new PrintWriter(new FileOutputStream("log.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}
		MySQLAccess.openConnection();
		double matrixDistributions [][] = new double [5][totWords];
		int word = 0;
		ResultSet resultSet = MySQLAccess.printData("SELECT ov1, ov2, ov3, ov4, ov5 FROM db_tripadvisor.tb_distributions WHERE 1");
		try {
			while (resultSet.next()){
				for (int i=0; i<5; i++){
					matrixDistributions[i][word] = Double.parseDouble(resultSet.getString(i+1));}
				word++;}
			resultSet.close();} 
		catch (SQLException e) {
			e.printStackTrace();}
		MySQLAccess.close();
		return matrixDistributions;
	}//End public static double [][] loadMatrixDistributions(int totWords)
	
	public static double [][] loadMatrixDistributionsBin(int totWords){ //Restituisce la tabella delle distribuzioni sotto forma di matrice
		try {
			Train.printWriterLog =  new PrintWriter(new FileOutputStream("log.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}
		MySQLAccess.openConnection();
		double matrixDistributions [][] = new double [5][totWords];
		int word = 0;
		ResultSet resultSet = MySQLAccess.printData("SELECT ov1, ov2 FROM db_tripadvisor.tb_distributions_bin WHERE 1");
		try {
			while (resultSet.next()){
				for (int i=0; i<2; i++){
					matrixDistributions[i][word] = Double.parseDouble(resultSet.getString(i+1));}
				word++;}
			resultSet.close();} 
		catch (SQLException e) {
			e.printStackTrace();}
		MySQLAccess.close();
		return matrixDistributions;
	}//End public static double [][] loadMatrixDistributions(int totWords)

}//End public class DbInterface
