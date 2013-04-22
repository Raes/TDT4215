package main;
import org.apache.lucene.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import java.io.*;
import indexers.*;

public class Main {

	 /* 
	  * Calls each class (NLH, ICD, ATC) which then indexes their respective
	  * files, adding to the Lucene file underneath each diseases heading.
	  * 
	  * When that is done it calls out to another class "insert-name-here"
	  * which then strips the case files and uses the indexed files to build a
	  * list of relevant chapters.
	  * 
	  * Afterwards it will use a final class that uses Lucene to rank the
	  * different hits and outputs the final list of relevant chapters as a
	  * result.
	 */
	
	public static void main(String[] args) throws IOException, ParseException{
		//Specifying analyzer for tokenizing text
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		
		//Creating index, I believe each class can add Documents now to it
		Directory index = new RAMDirectory();
		
		//Call NLH to index Norsk legemiddelhånboken
		NLH nlh = new NLH();
		nlh.indexNLH();
	}
}