package main;
import indexers.NLH;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

public class Main {

	 /* 
	  * Calls index class(es) which then indexes their respective
	  * files, adding to the Lucene file underneath each diseases heading.
	  * 
	  * When that is done it calls out to TableGenerator, this class
	  * then strips a case file and uses the indexed files to build a
	  * list of relevant chapters, outputting the ranked results to console.
	 */
	
	public static void main(String[] args) throws IOException, ParseException{
		
		//Call NLH to index Norsk legemiddelhånboken
//		NLH nlh = new NLH();
//		nlh.indexNLH();
		
		//Call TableGenerator to parse case files and create 'hit tables'
		//for our case files.
		
		TableGenerator tblGen = new TableGenerator();
		tblGen.generate();
		tblGen.generateByArray();
	}
}