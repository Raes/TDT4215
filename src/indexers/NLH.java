package indexers;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


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
				
				try {
					extractText(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*(Testing)
				 * file = listOfFiles[i].getName();
				 * System.out.println(file);
				*/
			}
		}
	}
	
	//Strips HTML tags from file, returns stripped String
	public String extractText(File f) throws IOException{
		Document doc = Jsoup.parse(f, "UTF-8", "");
		System.out.println(doc);
		
		return "";
	}
}
