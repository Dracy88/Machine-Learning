package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import core.Train;

/********************* Contiene procedure e funzioni di utilità del software *********************/

public class Gadget {
	
	public static void writeFile(String text){ //Scrive sul file tmp/stemmer.tmp una stringa
		PrintWriter printWrite = null;
		try {
			printWrite = new PrintWriter(new FileOutputStream("tmp/stemmer.tmp",false));
			printWrite.print(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printWrite.close();
	}//End public static void writeFile(String text)
	
	
	public static int [] resetWordX(int [] arr){ //Restituisce un array di zeri
		for (int i=0; i<arr.length; i++){
			arr[i] = 0;}
		return arr;
	}//End public static int [] resetWordX(int [] arr)
	

    public static double[][] normalizzaMatrix(double matrix[][], int [] crossValidation) throws FileNotFoundException{ //Crea una matrice con le distribuzioni di % normalizzate
		double matrixApp [][] = matrix; //forse lo togliamo
		Train.printWriterLog.println("Inizio normalize matrix");

		for (int col=0; col<matrix[0].length; col++){ //Per ciascuna word
			for (int row=0; row<5; row++){ //Per ogni vocabolo stima la distribuzione di % per quel valore di overall
				matrixApp[row][col] = matrix[row][col]/crossValidation[row];}}
		
		Train.printWriterLog.println("fine normalize matrix");
		
		return matrixApp;
	}//End public static double[][] normalizeMatrix(double matrix[][])
    
    public static int getTotReview(int [] crossValidation){
    	int sum =0;
    	for (int i=0; i<5; i++){
    		sum += crossValidation[i];}
    	return sum;
    }
    
    
	
	public static double[][] normalizeMatrix(double matrix[][]) throws FileNotFoundException{ //Crea una matrice con le distribuzioni di % normalizzate
		double matrixApp [][] = matrix; //forse lo togliamo
		Train.printWriterLog.println("Inizio normalize matrix");

		for (int col=0; col<matrix[0].length; col++){ //Per ciascuna word
			double sum = 0.0;
			for (int row=0; row<5; row++){ //Per ogni vocabolo somma quante volte è presente in totale (a prescindere dal overall)
				sum += matrix[row][col];}
			
			for (int row=0; row<5; row++){ //Per ogni vocabolo stima la distribuzione di % per quel valore di overall
				if (matrix[row][col]!=0){
					matrixApp[row][col] = (matrix[row][col]/sum);}}
			
		}//End for (int col=0; col<matrix[0].length; col++)
		Train.printWriterLog.println("fine normalize matrix");
		return matrixApp;
	}//End public static double[][] normalizeMatrix(double matrix[][])
	
	
    public static double[] normalize(double array[]) throws FileNotFoundException{ //Crea una matrice con le distribuzioni di % normalizzate
		double arrayApp [] = array; //forse lo togliamo
		Train.printWriterLog.println("Inizio normalize array");
		double sum = 0;

		for (int col=0; col<array.length; col++){ //Per ogni vocabolo somma quante volte è presente in totale (a prescindere dal overall)
			sum += array[col];}

		for (int col=0; col<array.length; col++){ //Per ogni vocabolo somma quante volte è presente in totale (a prescindere dal overall)
		    arrayApp[col]/=sum;}
		
		Train.printWriterLog.println("fine normalize array");
		return arrayApp;
	}//End public static double[][] normalizeMatrix(double matrix[][])
	
	
	public static double approximate(double x, int precision){ //Approssima la matrice alla n-esima cifra decimale scelta
		return (Math.floor(x*Math.pow(10.0, precision)))/Math.pow(10.0, precision);}

	
	public static double [] resetArray(){ //Inizializza l'array delle classi per la stima bernulliana
		double array[] = {1.0, 1.0, 1.0, 1.0, 1.0};
		return array;
	}//End public static double [] resetArray()
	
	
	public static void stop(){ //Procedura provvisoria che permette di fermarsi in un qualsiasi punto del programma		
		Scanner key = new Scanner(System.in);
		System.out.println("STOP!");
		playSound("");
		key.next();
	}//End public static int minCrossValidation(int crossValidation[])
	
	
    public static int estimateOverall(double estimateDistribution[]){ //Restituisce il voto più probabile a partire dalle stime già effettuate
		double max = estimateDistribution[0];
		int c=1;
		for (int i=1; i<5; i++){
			if (max<estimateDistribution[i]){
				max=estimateDistribution[i];
				c=i+1;}}
		return c;
	}//End public static int estimateOverall(double estimateDistribution[])

    
	public static synchronized void playSound(final String url) { //Metodo provvisorio 
		new Thread(new Runnable() {
		    public void run() {
		      try {
		        Clip clip = AudioSystem.getClip();
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Gadget.class.getResourceAsStream("/sound/alert.wav" + url));
		        clip.open(inputStream);
		        clip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());}
		    }
		  }).start();
	}//End public static synchronized void playSound(final String url) 

	
	public static double[][] inizializeMatrix(double matrix[][]){ //Inizializza la matrice delle distribuzioni per evitare di avere degli zeri nella stessa
		for (int col=0; col<matrix[0].length; col++){
			for (int row=0; row<5; row++){
				matrix[row][col] = 0.01;}}
		return matrix;	
	}//End 	public static double[][] inizializeMatrix(double matrix[][]){

	public static int [] arrToBin(int wordX []){
		for (int i=0; i<wordX.length;i++){
			if (wordX[i]>0){
				wordX[i]=1;}}
		return wordX;
	}//End public static int [] arrToBin(int wordX [])
	
	public static int [] binaryOverall(int wordX []){
		for (int i=0; i<wordX.length; i++){
			if (wordX[i]>=3){
				wordX[i]=1;}
			else{
				wordX[i]=0;}
		}//End for (int i=0; i<wordX.length;i++)
		return wordX;
	}//End public static int [] arrToBin(int wordX [])
	
	public static void inizialize(){ //Procedura di reset del DB e dei files utilizzati per il training	
		File datFile = new File("out.txt");
		datFile.delete();
		datFile = new File("log.txt");
		datFile.delete();
		datFile = new File("tmp/out1.txt");
		datFile.delete();
		datFile = new File("tmp/out2.txt");
		datFile.delete();
		datFile = new File("tmp/out3.txt");
		datFile.delete();
		datFile = new File("tmp/out4.txt");
		datFile.delete();
		datFile = new File("tmp/out5.txt");
		datFile.delete();
		DbInterface.inizializeDB();
	}//End public static void inizialize()
	
	
	public static int minCrossValidation(int crossValidation[]){ //Trova il lower bound delle classi di overall	
		int min = crossValidation[0];
		for (int c=1; c<5; c++){
			if (min>crossValidation[c]){
				min=crossValidation[c];}}
		return min;
	}//End public static int minCrossValidation(int crossValidation[])
	
}//End public class Gadget
