package indexers;
import org.apache.lucene.*;
import java.io.*;


public class NLH {
	/* 
	 * Retrieves each NLH chapter and indexes diseases, adding relevant
	 * attributes to the Lucene file.
	 */
	public static void indexNLH(){
		
		//Lists all files in data/nlh directory
		String path = "./data/nlh";
		String file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for(int i=0; i<listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i].getName();
				System.out.println(file);
			}
		}
		
		//Imports .htm file 
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("/T1.1.htm"));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		String content = contentBuilder.toString();
		
	}
	
	//Strips HTML tags from string, returns stripped String
	public String strip(String s){
		String strippedString = "";
		
		
		return strippedString;
	}
}
