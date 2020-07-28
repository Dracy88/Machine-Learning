package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import lexical.PorterStemmer;
import lexical.StopWords;

import util.DbInterface;
import util.Gadget;
import util.MySQLAccess;

import lexical.Lemmer;

public class Test {
	
    static PrintWriter printWriter;
    static Map<String, String> map = new HashMap<String, String>();

	public static void testing(String baseDir, String [] listFiles) throws SQLException, FileNotFoundException{
		
		String strLinea = "";
		String strResult = "";
		Map<String, String> mapStopWords = new HashMap<String, String>();

		mapStopWords = MySQLAccess.mapping("SELECT id, value FROM tb_stopwords WHERE 1");
	    map = MySQLAccess.mapping("SELECT id, value FROM tb_dictionary WHERE 1");

		int numReview = 0;
		int totReview = 0; 
		float c = 1;
		boolean validReview = false;
		
		MySQLAccess.openConnection();
		inizialize();
		
		int totWords = MySQLAccess.getTotalDictWords("SELECT COUNT(id) FROM tb_dictionary");
		//int totWords = MySQLAccess.getTotalDictWords("SELECT COUNT(id) FROM tb_dictionary WHERE idf>=10");

		int wordX [] = new int [totWords]; //Indica la presenza o meno di ciascun termine del dizionario nella recensione
		double matrixDistributions[][] = DbInterface.loadMatrixDistributions(totWords);

		MySQLAccess.close();
		BufferedReader inputStream;
		
		try {
			printWriter =  new PrintWriter(new FileOutputStream("outReviews.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}
		
		for (int i=0; i<listFiles.length; i++){ //Carica ogni files della directory selezionata
			strResult = "";
			MySQLAccess.openConnection();
				try {
					inputStream = new BufferedReader(new FileReader(baseDir+"\\"+listFiles[i]));
					strLinea = inputStream.readLine();
					numReview=1;
					System.out.println("Analisi lessicale del file " + baseDir+"\\"+listFiles[i]);
					
					while (strLinea!=null){	
						if (strLinea.contains("<Content>") && !strLinea.contains("showReview")){
						//if (strLinea.contains("<Content>")){
							wordX = Gadget.resetWordX(wordX);
							try{
								strResult = strLinea.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+", ""); //Rimozione di eventuali indirizzi email
								strResult = strResult.replaceAll("[a-zA-Z0-9._%+-]+@", ""); //Rimozione di eventuali indirizzi email
								strResult = strResult.replaceAll("@[a-zA-Z0-9._%+-]+", ""); //Rimozione di eventuali indirizzi email
								strResult = strResult.replaceAll("[,\\n\\t()\\[\\]:.;\'?\\#`&\"_+=$!*{}~%]+", " "); 
							}//End try
							catch (Exception e){
								System.out.println("errore in " + strResult);}
							strResult = StopWords.removeStopWords2(strResult.replaceFirst("<Content>", ""), mapStopWords); //Rimozione delle stopwords
							strResult = strResult.replaceAll("\\|", "");
							if (strResult!=""){
								//strResult = PorterStemmer.doStemming(strResult);
								strResult = Lemmer.doLemming(strResult);
								strResult = StopWords.removeStopWords2(strResult, mapStopWords); //Rimozione delle stopwords
								if (strResult!=null & strResult!=""){
									printWriter.print((listFiles[i].replaceAll("hotel_", "")).replaceAll(".dat", "") + "|" + numReview++ + "|" + strResult + "|");
									validReview = true;
									wordX = (wordsPresence(strResult, totWords));				
								}//End if (strResult!=null & strResult!="")
							}//End if (strResult!=null)
						}//End if (strLinea.contains("<Content>"))
						
						if (strLinea.contains("<Overall>") & validReview){
							validReview = false;
							Short overall = 0;
							overall = Short.parseShort(strLinea.replaceAll("<Overall>", ""));
							printWriter.println();							
							//MySQLAccess.push("INSERT INTO db_tripadvisor.tb_result (hotel_id, review_id, review, overall, estimate_overall) VALUES ('" + listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", "") + "', " + (numReview-1) + ", '" + strResult + "', " + overall + ", " + Gadget.estimateOverall(nbClassifierBernulli(matrixDistributions, wordX, totWords)) + ")");                                             
							totReview++;
							MySQLAccess.push("INSERT INTO db_tripadvisor.tb_result (hotel_id, review_id, review, overall, estimate_overall_multinomial, estimate_overall_bernoulli) VALUES ('" + 
							        listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", "") + "', " + (numReview-1) + ", '" + strResult + "', " + overall + ", " + 
									Gadget.estimateOverall(nbClassifierMultinomial(matrixDistributions, wordX, totWords)) + ", " + Gadget.estimateOverall(nbClassifierBernulli(matrixDistributions, arrToBin(wordX), totWords)) + ")");                                             

							printWriter.flush();}
						strLinea=inputStream.readLine();		
					}//End while (strLinea!=null)
					
				}//End try
				catch (Exception e) {
					e.printStackTrace();}		
				
				System.out.println("Progress complessivo: " + (double) ((c++/listFiles.length)*100) + "%");
				MySQLAccess.close();

		}//End for (int i=0; i<listFiles.length; i++)
		
		System.out.println("Numero review valide analizzate: " + totReview);

	}//End public static void training(String baseDir, String [] listFiles) throws SQLException, FileNotFoundException
	
	
	public static int[] wordsPresence (String text, int totWords){ //Restituisce un array che indica quali e quanti vocaboli del dizionario compaiono nella recensione
		StringTokenizer tokens = new StringTokenizer(text.toLowerCase(), ",	/ -\n\t():.;\'?\\#`&\"_+=$!*");
		String str = new String();
		int wordX[] = new int [totWords];
		while (tokens.hasMoreElements()) {
			str = tokens.nextElement().toString();
			if (map.containsKey(str)){ 
				wordX[Integer.parseInt(map.get(str))-1]++;}}
		return wordX;
	}//End public static int[] wordsPresence (String text, int totWords)
	
	
	public static double [] nbClassifierBernulli (double matrixDistributions[][], int wordX [], int totWords){ //Classificatore Naive Bayes Bernulliano binario
		try {
			Train.printWriterLog =  new PrintWriter(new FileOutputStream("log.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}

		double arrClass[] = new double[]{1, 1, 1, 1, 1};

		for (int idClass=0; idClass<5; idClass++){
			for (int word=0; word<totWords; word++){
				arrClass[idClass]*= Math.pow(matrixDistributions[idClass][word], wordX[word]) * Math.pow(1 - matrixDistributions[idClass][word], (1 - wordX[word]));}}
		
		return arrClass;

	}//End public static double [] nbClassifierBernulli (double matrixDistributions[][], int wordX [], int totWords)
	
	
	public static double [] nbClassifierMultinomial (double matrixDistributions[][], int wordX [], int totWords){ //Classificatore Naive Bayes Multi nomiale
		try {
			Train.printWriterLog =  new PrintWriter(new FileOutputStream("log.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}
		
		double arrClass [] = DbInterface.getPriorProbability();

		for (int idClass=0; idClass<5; idClass++){
			for (int word=0; word<totWords; word++){
				if (wordX[word]>0){
					arrClass[idClass]*= Math.pow(matrixDistributions[idClass][word], wordX[word]);}}}
		
		return arrClass;
	}//End public static double [] nbClassifierBernulli (double matrixDistributions[][], int wordX [], int totWords)
	
	
	public static int arrayToBin(int wordX){ //Converte in binario una matrice 
		return (wordX>=1) ? 1 : 0;}
	
	
	public static int [] arrToBin(int wordX []){
		for (int i=0; i<wordX.length;i++){
			if (wordX[i]>0){
				wordX[i]=1;}}
		return wordX;
	}//End public static int [] arrToBin(int wordX [])
	
	
	public static void inizialize(){ //Procedura di inizializzazione per il testing
		File datFile = new File("outReviews.txt");
		datFile.delete();
		datFile = new File("log.txt");
		datFile.delete();
		MySQLAccess.push("TRUNCATE tb_result");
	}//End public static void inizialize()

}//End public static void testing(String baseDir, String [] listFiles)
