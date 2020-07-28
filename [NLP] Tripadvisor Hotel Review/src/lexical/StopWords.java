package lexical;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import util.MySQLAccess;


public class StopWords {
	
	public static String removeNumber(String text){ //Rimuove tutti i numeri da una recensione
		return text.replaceAll("[0-9]", "");
	}//End public static String removeNumber(String text)

	
	public static String removeStopWords2(String text, Map map) throws SQLException{ //Dato un testo in ingresso, restituisce il medesimo privo di stopwords
		
		StringTokenizer tokens = null;
		String concat = "";
		String result = "";
		try {
			if (text!=null){
				tokens = new StringTokenizer(removeNumber(text.toLowerCase()), ",	/ -\n\t():.;\'?\\#`&\"_+=$!*");
				while (tokens.hasMoreElements()) {
					concat = tokens.nextElement().toString();
					if (!map.containsKey(concat)){ //Se il token non è una stopword, allora lascialo stare
						result = result + concat + " ";}
				}//End while (tokens.hasMoreElements())}
			}//End if (text!=null){
		}//End try
		catch (Exception e2) {}
		return result.trim();
	}//End 	public static String removeStopWords2(String text, Map map) throws SQLException{

}//End public class StopWords
