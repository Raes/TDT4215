package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CaseParser {
	
	public static final String STOPWORD_LOCATION = "data/stopwords.txt";
	
	/*
	 * This class receives a case file from TableGenerator, parses and stems the case file
	 * and returns a list of terms we want to search for.
	 */
	public static boolean isVowel(char letter){
		char vowels[] = new char[]{'a', 'e', 'i', 'o', 'u', 'y', '¾', '¿', 'Œ'};
		for (char vowel : vowels){
			if(vowel == letter){
				return true;
			}
		}
		return false;
	}
	
	public static int r1StartsAt(String word){
		int length = word.length();
		boolean firstNonVowelAfterVowelReached = false;
		boolean firstVowelReached = false;
		
		for(int i = 0; i < length; i++){
			if(isVowel(word.charAt(i))){
				firstVowelReached = true;
			}
			else if(firstVowelReached && (!isVowel(word.charAt(i)))){
				firstNonVowelAfterVowelReached = true;
			}
			
			if(firstVowelReached && firstNonVowelAfterVowelReached){
				return i+1;
			}
		}
		return length;
	}
	
	public static boolean validSEnding(String word){
		char letters[] = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'j', 'l', 'm', 'n', 'o', 'p', 'r', 't', 'v', 'y', 'z'};
		char endingLetter = word.charAt(word.length()-2);
		
		if(endingLetter == 'k'){
			if(!isVowel(word.charAt(word.length()-3))){
				return true;
			}
		}
		for (int i = 0; i < word.length(); i++){
			if(endingLetter == letters[i]){
				return true;
			}
		}
		
		return false;
	}
	
	public static String stemming(String word){
//		Step 1
		String stepOneA[] = new String[]{"a", "e", "ede", "ande", "ende", "ane", "ene", "hetene", "en", "heten", "ar", "er", "heter", "as", "es", "edes", "endes", "ens", "hetens", "ers", "ets", "et", "het", "ast"};
		String stepOneB = new String("s");
		String stepOneC[] = new String[]{"erte", "ert"};
		int matchedSuffixLength = 0;
		String r1 = word.substring(r1StartsAt(word));
		
		for(String suffix : stepOneA){
			if(word.substring(suffix.length()).equals(suffix)){
				matchedSuffixLength = suffix.length();
			}
		}
		
//		Step 2
		String stepTwo[] = new String[]{"dt", "vt"};
		
//		Step 3
		String stepThree[] = new String[]{"leg", "eleg", "ig", "eig", "lig", "elig", "els", "lov", "elov", "slov", "hetslov"};
		
		return word;
	}
	
	public static boolean isStopWord(String word) throws IOException,FileNotFoundException{
		String readLine = new String("");
		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		
		try{
			fis = new FileInputStream(STOPWORD_LOCATION);
			dis = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(dis));
		}catch(FileNotFoundException e){
			System.out.println("Error: " + e.getMessage());
		}
		
		while((readLine = br.readLine()) != null){
			if(readLine.equals(word)){
				return true;
			}
		}
		return false;
	}
	
	public static String parseFile(File caseFile) throws IOException, FileNotFoundException{
		String parsedString = new String("");
		String readLine = new String("");
		Scanner scanner = null;
		
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(caseFile));
		}catch(FileNotFoundException e){
			System.out.println("Error: " + e.getMessage());
		}
		
		while((readLine = br.readLine()) != null){
			if(readLine.charAt(0) != '#'){
				scanner = new Scanner(readLine);
				while(scanner.hasNext()){
					
				}
			}
		}
		br.close();
		return parsedString;
	}
}
