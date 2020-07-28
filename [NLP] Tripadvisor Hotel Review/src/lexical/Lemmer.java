package lexical;

import java.util.Arrays;

import edu.stanford.nlp.simple.*;

public class Lemmer{
	
	public static String [] allowedTags = {
			"NN", "NNP", "NNPS", "NNS",		// Nomi
			"RB", "RBR", "RBS",				// Avverbi
			"JJ", "JJR", "JJS"				// Aggettivi
			//	Verbi, blocco da decommentare solo ai fini di test
			,"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"
 			
	};
	
	public static boolean sorted = false;
	
	public static String doLemming(String srcText){
		if(!sorted){
			Arrays.sort(allowedTags, null);
			sorted = true;
		}
		Document doc = new Document(srcText);
		String result = "";
		for(Sentence sent: doc.sentences()){
			int i = 0;
			for(String lemma: sent.lemmas()){
				if(Arrays.binarySearch(allowedTags, sent.posTag(i), null) >= 0){
					if(result != ""){
						result += " ";
					}
					result += lemma;
				}
				i++;
			}
		}
		return result;
	}//End public static String doLemming(String srcText)

	public static void main(String [] args){
		String [] test = {
			"The big brown fox jumped over the lazy dog.",
			"The turtle was always ahead of Achille",
			"Despite that nasty hotel the landscape was beautiful."
		};
		for(String sent: test){
			System.out.println("Sentence: " + sent + "\nLemmatization: " + doLemming(sent));
		}
	}
	
}//End public class Lemmer