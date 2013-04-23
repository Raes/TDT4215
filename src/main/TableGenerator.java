package main;

import java.io.File;

public class TableGenerator {
	
	/*
	 * This class does two things, first it calls out to CaseParser with a case
	 * file and receives a list of terms we want to search for from that case.
	 * 
	 * Then it uses those terms to search through the Lucene disease data files
	 * in /data/nlh_index. Upon receiving hits the class then ranks and generates
	 * a file with a table of hits.
	 */
	
	public void generate(){
		
		//Retrieves folder of case files, puts into File list.
		String path = "./data/cases";
		File file = null;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		//Empty string, return from calling Case Parser will go here
		String terms = null;
		
		//For each case file, do something.
		for(int i=0; i<listOfFiles.length; i++){
			
		}
		
	}

}
