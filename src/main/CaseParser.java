package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
		char endingLetter = ' ';
		if(word.length() > 2){
			endingLetter = word.charAt(word.length()-2);
		}
		else{
			return false;
		}
		if(endingLetter == 'k'){
			if(!isVowel(word.charAt(word.length()-3))){
				return true;
			}
		}
		for (int i = 0; i < letters.length; i++){
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
		boolean stepOneAMatch = false;
		boolean stepOneBMatch = false;
		boolean stepOneCMatch = false;
		boolean stepThreeMatch = false;
		int matchedSuffixLength = 0;
		int addressOfMatchedSuffix = 0;
		
		for(int i = 0; i < stepOneA.length; i++){
			if(word.endsWith(stepOneA[i])){
				if(matchedSuffixLength < (word.substring(stepOneA[i].length()).length())){
					matchedSuffixLength = stepOneA[i].length();
					addressOfMatchedSuffix = i;
					stepOneAMatch = true;
				}
			}
		}
		if(word.endsWith("s") && (!stepOneAMatch)){
			stepOneBMatch = true;
		}
		for(int i = 0; i < stepOneC.length; i++){
			if(word.endsWith(stepOneC[i])){
				matchedSuffixLength = stepOneC[i].length();
				addressOfMatchedSuffix = i;
				stepOneCMatch = true;
			}
		}
		if(stepOneAMatch){
			word = word.substring(0,(word.length()-stepOneA[addressOfMatchedSuffix].length()));
		}
		else if(stepOneBMatch){
			if(validSEnding(word)){
				word = word.substring(0,(word.length()-stepOneB.length()));
			}
		}
		else if(stepOneCMatch){
			word = word.substring(0,(word.length()-stepOneC[addressOfMatchedSuffix].length()));
		}
		
//		Step 2
		if(word.endsWith("dt")){
			word = word.substring(0,word.length()-3);
		}
		else if(word.endsWith("vt")){
			word = word.substring(0, word.length()-3);
		}
		
//		Step 3
		String stepThree[] = new String[]{"leg", "eleg", "ig", "eig", "lig", "elig", "els", "lov", "elov", "slov", "hetslov"};
		for(int i = 0; i < stepThree.length; i++){
			if(word.endsWith(stepThree[i])){
				if(matchedSuffixLength < (word.substring(stepThree[i].length()).length())){
					matchedSuffixLength = stepThree[i].length();
					addressOfMatchedSuffix = i;
					stepThreeMatch = true;
				}
			}
		}
		if(stepThreeMatch){
			word = word.substring(0,(word.length()-stepThree[addressOfMatchedSuffix].length()));
		}
		
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
				fis.close();
				dis.close();
				br.close();
				return true;
			}
		}
		fis.close();
		dis.close();
		br.close();
		return false;
	}
	
	public static String cleanWord(String word){
		word = word.replaceAll("[^a-zA-Z\\-\\¾\\¿\\Œ\\®\\¯\\]","");
		return word;
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
			scanner = new Scanner(readLine);
			while(scanner.hasNext()){
				String word = scanner.next();
				word = cleanWord(word);
				word = word.toLowerCase();
				
				if(isStopWord(word)){
					continue;
				}
				word = stemming(word);
				parsedString += word;
				if(scanner.hasNext()){
					parsedString += " ";
				}
			}
		}
		br.close();
		return parsedString;
	}
	
	public static ArrayList<String> parseFileArray(File caseFile) throws IOException, FileNotFoundException{
		ArrayList<String> array = new ArrayList<String>();
		String parsedString = new String("");
		String readLine = new String("");
		String word = "";
		Charset encoding = Charset.defaultCharset();
		Scanner scan = null;
		InputStream in = null;
		Reader reader = null;
		int r = 0;
		
		try{
			in = new FileInputStream(caseFile);
			reader = new InputStreamReader(in, encoding);
		}catch(IOException e){
			System.out.println("Error: " + e.getMessage());
		}
		while((r = reader.read()) != -1){
			if(readLine.equals("") && r == 32){
//				do nothing
			}
			else if(r == 46){
				array.add(readLine);
				readLine = "";
			}
			else{
				readLine += (char)r;
			}
		}
		
		for(int i = 0; i < array.size(); i++){
			readLine = array.get(i);
			scan = new Scanner(readLine);
			while(scan.hasNext()){
				word = scan.next();
				word = cleanWord(word);
				word = word.toLowerCase();
				if(isStopWord(word)){
					continue;
				}
				word = stemming(word);
				parsedString += word;
				if (scan.hasNext()){
					parsedString += " ";
				}
			}
//			System.out.println(parsedString);
			array.set(i, parsedString);
			parsedString = "";
		}
		
		return array;
	}
}
