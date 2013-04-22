package indexers;
import java.io.File;


public class NLH {
	/* 
	 * Retrieves each NLH chapter and indexes diseases, adding relevant
	 * attributes to the Lucene file.
	 */
	public void indexNLH(){
		
		//Lists all files in data/nlh directory
		String path = "./data/nlh";
		File file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		/*
		 * For each file, do something. Here will will send to HTML stripper
		 * in preparation for indexing.
		 */
		for(int i=0; i < listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i];
				strip(file);
				/*(Testing)
				 * file = listOfFiles[i].getName();
				 * System.out.println(file);
				*/
			}
		}
	}
	
	//Strips HTML tags from string, returns stripped String
	public String strip(File f){
		
		return "";
	}
}
