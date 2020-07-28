package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import lexical.Lemmer;
import lexical.PorterStemmer;
import lexical.StopWords;
import util.DbInterface;
import util.Gadget;
import util.MySQLAccess;

/**************************** Effettua il training a partire da un dataset ****************************/

public class Train {
	
	static PrintWriter printWriter;
	static PrintWriter printWriterC1;
	static PrintWriter printWriterC2;
	static PrintWriter printWriterC3;
	static PrintWriter printWriterC4;
	static PrintWriter printWriterC5;

	public static PrintWriter printWriterLog;
	public static int [] overallTot = new int [5];

	public static void training(String baseDir, String [] listFiles) throws SQLException, FileNotFoundException{ //Procedura principale di training
		
		String strLinea = "";
		String strResult = "";
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapStopWords = new HashMap<String, String>();
		mapStopWords = MySQLAccess.mapping("SELECT id, value FROM tb_stopwords WHERE 1");

		int numReview = 0;
		int ignoredReview = 0;
		//int crossValidationValue = 0;
		//int crossValidation [] = new int [5];
		int crossValidation [] = {1, 1, 1, 1, 1};

		BufferedReader inputStream;
		DbInterface.inizializeDB();
		Gadget.inizialize();
		
		/************************************************** Cross Validation **************************************************/
		
		System.out.println("Inizio cross validation del dataset..");
		
		try {
			printWriterC1 =  new PrintWriter(new FileOutputStream("tmp/out1.txt", true));
			printWriterC2 =  new PrintWriter(new FileOutputStream("tmp/out2.txt", true));
			printWriterC3 =  new PrintWriter(new FileOutputStream("tmp/out3.txt", true));
		    printWriterC4 =  new PrintWriter(new FileOutputStream("tmp/out4.txt", true));
	        printWriterC5 =  new PrintWriter(new FileOutputStream("tmp/out5.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}

		for (int i=0; i<listFiles.length; i++){
			String textReview = "";
			int idReview = 1;
			try {
				inputStream = new BufferedReader(new FileReader(baseDir+"\\"+listFiles[i]));
				strLinea = inputStream.readLine();
				while(strLinea!=null){
					if (strLinea.contains("<Content>")){
						textReview = strLinea.replaceAll("<Content>", "");}
					if (strLinea.contains("<Overall>")){
						switch (Integer.parseInt(strLinea.replaceAll("<Overall>", ""))){
							case 1: 
								if (!textReview.contains("showReview")){
									 printWriterC1.println(listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", ""));
									 printWriterC1.println(idReview++);
									 printWriterC1.println(textReview);
									 printWriterC1.flush();
									 crossValidation[0]++;}
								break;
							
							case 2: 
								if (!textReview.contains("showReview")){
									 printWriterC2.println(listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", ""));
									 printWriterC2.println(idReview++);
									 printWriterC2.println(textReview);
									 printWriterC2.flush();
									 crossValidation[1]++;}
							    break;

							case 3: 
								if (!textReview.contains("showReview")){
									printWriterC3.println(listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", ""));
									printWriterC3.println(idReview++);
									printWriterC3.println(textReview);
									printWriterC3.flush();
									crossValidation[2]++;}
							    break;

							case 4: 
								if (!textReview.contains("showReview")){
									printWriterC4.println(listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", ""));
									printWriterC4.println(idReview++);
									printWriterC4.println(textReview);
									printWriterC4.flush();
									crossValidation[3]++;}
							    break;

							case 5: 
								if (!textReview.contains("showReview")){
									printWriterC5.println(listFiles[i].replaceAll("hotel_", "").replaceAll(".dat", ""));
									printWriterC5.println(idReview++);
									printWriterC5.println(textReview);
									printWriterC5.flush();	
									crossValidation[4]++;}
							    break;

						}; //End switch (Integer.parseInt(strLinea.replaceAll("<Overall>", ""))
					} //End if (strLinea.contains("<Overall>"))
					strLinea = inputStream.readLine(); 
				} //End while(strLinea!=null){	
			} 
			catch (IOException e) {
				e.printStackTrace();}
			
			System.out.println((((double)(i+1)/listFiles.length)*100) + "% completata..");
		} //for (int i=0; i<listFiles.length; i++)
		
		//crossValidationValue = Gadget.minCrossValidation(crossValidation);

		printWriterC1.close();
		printWriterC2.close();
		printWriterC3.close();
		printWriterC4.close();
		printWriterC5.close();
		System.out.println("Termine cross validation del dataset..");
		System.out.println("***************************************");
		int totReview = Gadget.getTotReview(crossValidation);


		/************************************************ End Cross Validation ************************************************/
		
		/************************************************* Analisi lessicale *************************************************/

		System.out.println("num ov1: " + crossValidation[0] + "num ov2: " + crossValidation[1] + "num ov3: " + crossValidation[2] + "num ov4: " + crossValidation[3] + "num ov5: " + crossValidation[4]);
		//Gadget.stop();
		System.out.println("Inizio analisi lessicale");
		try {
			printWriterLog =  new PrintWriter(new FileOutputStream("log.txt", true));
			printWriter =  new PrintWriter(new FileOutputStream("out.txt", true));} 
		catch (FileNotFoundException e) {
			e.printStackTrace();}
		
		double progress = 1.0;
		
		for (int i=1; i<=5; i++){ 
			String hotelId = "";
			String reviewId = "";
			MySQLAccess.openConnection();
			StringTokenizer tokens = null;

			inputStream = new BufferedReader(new FileReader("tmp/out" + i + ".txt"));
			System.out.println("Analisi lessicale delle recensioni di classe " + i);
			printWriterLog.println("Analisi lessicale delle recensioni di classe " + i);
			
			for (int k=0; k<crossValidation[i-1] - 1; k++){
				strLinea = "";
				numReview=1;
				try {
					hotelId = inputStream.readLine();
					reviewId = inputStream.readLine();
					strLinea = inputStream.readLine();} 
				catch (IOException e) {
					e.printStackTrace();}
				
				//System.out.println("hotel id: " + hotelId + " review id: " + reviewId + " strLinea: " + strLinea);
				strResult = textPreProcessing(strLinea, mapStopWords);
						
				System.out.println(((progress++)/totReview)*100 + "% completata! ");

				if (strResult!=""){ //Se la recensione conteneva solo stopwords, allora non inserirla
					printWriter.println(hotelId + "|" + reviewId + "|" + strResult + "|" + i);
					DbInterface.loadIntoDictionary2(strResult, Integer.parseInt(reviewId), hotelId);}
			}//End for (int k=0; k<crossValidationValue; k++){
			
			MySQLAccess.close();

		}//End for (int i=0; i<=5; i++)
		
		System.out.println("Analisi lessicale terminata!");
		System.out.println("Numero files analizzati: " + listFiles.length);
		System.out.println("Numero recensioni analizzate: " + totReview);
		System.out.println("Creazione del dizionario in corso...");
		DbInterface.createDict();
		System.out.println("Dizionario creato!");
		
		printWriterLog.println("Dizionario creato!");
		printWriter.close();
		
		/*********************************************** End Analisi lessicale ***********************************************/
		
		/******************************************* Caricamento Matrice Word/Rank *******************************************/
		
		MySQLAccess.openConnection();
		
		int totWordsDict = MySQLAccess.getTotalDictWords("SELECT COUNT(id) FROM tb_dictionary");
		System.out.println("totale: " + totWordsDict);
		//short matrixDistributions[][][] = new short [5][MySQLAccess.getTotalDictWords("SELECT COUNT(id) FROM tb_dictionary")][totReview];
		double matrixDistributions2[][] = new double [5][totWordsDict];
		matrixDistributions2 = Gadget.inizializeMatrix(matrixDistributions2);

		MySQLAccess.close();

		StringTokenizer tokens = null;
		StringTokenizer tokenWord = null;
		numReview = 1;
		
		System.out.println("Mapping delle parole del dizionario..");
		
		map = MySQLAccess.mapping("SELECT id, value FROM tb_dictionary WHERE 1");
		try {	
			inputStream = new BufferedReader(new FileReader("out.txt"));
			strLinea=inputStream.readLine();
			tokens = new StringTokenizer(strLinea, "|");
			System.out.println("Creazione della matrice dei vocaboli");
			printWriterLog.println("Creazione della matrice dei vocaboli");	

			while (strLinea!=null){
				while (tokens.hasMoreElements()){ //Scansiona ciascuna recensione
					strLinea = tokens.nextToken(); //ID Hotel
					strLinea = tokens.nextToken(); //ID Review
					strLinea = tokens.nextToken(); //Review
					tokenWord = new StringTokenizer(strLinea, " ");
					try{
						short arrayDistributions[] = new short [totWordsDict];

						short overall = Short.parseShort(tokens.nextToken()); //Overall
						while (tokenWord.hasMoreElements()){ //Scansione solo il testo della recensione
							strLinea = tokenWord.nextToken();
							printWriterLog.println(strLinea);		
							if (map.containsKey(strLinea)){
								arrayDistributions[Integer.parseInt(map.get(strLinea)) - 1]=1;}
							//printWriterLog.println("metrix" + matrixDistributions[overall-1][Integer.parseInt(map.get(strLinea)) - 1]);	
						}//End while (tokenWord.hasMoreElements())

						for (int col=0; col<arrayDistributions.length; col++){
								matrixDistributions2[overall-1][col]+=arrayDistributions[col];}
						
					}//End Try
					catch(Exception e){}
				
				    strLinea=inputStream.readLine();
				    System.out.println(numReview + "° review (" + (double) numReview/totReview*100 + "% completato..)");
				    printWriterLog.println(numReview + "° review (" + (double) numReview++/totReview*100 + "% completato..)");
				}//End while (tokens.hasMoreElements())
				
				if (strLinea!=null){
					tokens = new StringTokenizer(strLinea, "|");}
				
			}//End while (strLinea!=null)
			
		}//End try 
		catch (IOException e) {
			e.printStackTrace();}
		
		/*********************************** End Caricamento Matrice Word/Rank ***********************************/
		
		System.out.println("Numero rec: " + totReview);
		printWriterLog.println("Numero rec: " + totReview);		

		System.out.println("Normalizzazione della matrice di distribuzione...");
		printWriterLog.println("Normalizzazione della matrice di distribuzione...");	
		
		MySQLAccess.openConnection();

		DbInterface.saveMatrix(Gadget.normalizzaMatrix(matrixDistributions2, crossValidation)); //Salvataggio della distribuzione di % p(x|c)
		DbInterface.savePrior(crossValidation, totReview);
		//DbInterface.saveMatrix(matrixDistributions2); //Salvataggio della distribuzione di % p(x|c)

		MySQLAccess.push("TRUNCATE tb_dictionary_raw"); //Svuota la tabella temporanea
		MySQLAccess.push("TRUNCATE tb_dictionary_tmp"); //Svuota la tabella temporanea

		MySQLAccess.close();
		System.out.println("Training Completato!");	
		printWriterLog.println("Training Completato!");	

		printWriterLog.close();
	
	}//End public static void training(String baseDir, String [] listFiles) throws SQLException, FileNotFoundException
	
	public static String textPreProcessing(String text, Map mapStopWords) throws SQLException{ //Restituisce un testo effettuando lo stemming, il lemming ed elimina le stopwords e caratteri strani
		String strResult = "";
		strResult = text.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+", ""); //Rimozione di eventuali indirizzi email
		strResult = strResult.replaceAll("[a-zA-Z0-9._%+-]+@", ""); //Rimozione di eventuali indirizzi email
		strResult = strResult.replaceAll("@[a-zA-Z0-9._%+-]+", ""); //Rimozione di eventuali indirizzi email
		strResult = strResult.replaceAll("[,\\n\\t()\\[\\]:.;\'?\\#`&\"_+=$!*{}~%]+", " "); 
		strResult = strResult.replaceAll("\\|", "");
		
		strResult = StopWords.removeStopWords2(strResult, mapStopWords); //Rimozione delle stopwords
		strResult = Lemmer.doLemming(strResult);
		//strResult = PorterStemmer.doStemming(strResult);
		strResult = StopWords.removeStopWords2(strResult, mapStopWords);
		
		return strResult;
	}//End public static String textPreProcessing(String text, Map mapStopWords)

}//End Train
