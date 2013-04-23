package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

public class TableGenerator {
	
	/*
	 * This class does two things, first it calls out to CaseParser with a case
	 * file and receives a list of terms we want to search for from that case.
	 * 
	 * Then it uses those terms to search through the Lucene disease data files
	 * in /data/nlh_index. The top 10 result hits are returned and the output
	 * is show in console.
	 */
	public static final String INDEX_LOCATION = "data/nlh_index";
	
	public void generate(){
		
		//Retrieves folder of case files, puts into File list.
		String path = "./data/cases";
		File file = null;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();		
		//Empty string, return from calling Case Parser will go here
		String terms = null;
		
		//For each case file, send to parser.
		for(int i=0; i<listOfFiles.length; i++){
			
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i];
			}
			//Should send casefile to parser and receive String of words in return.
			try {
				terms = CaseParser.parseFile(file);
			} catch (FileNotFoundException e) {
				System.out.println("main/TableGenerator.java/generate() FileNotFound exception sending casefile to CaseParser.parseFile()");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("main/TableGenerator.java/generate() IOException sending casefile to CaseParser.parseFile()");
				e.printStackTrace();
			}
			
			//Send term list to search()
			try {
				System.out.println("Case file# " + i + " top 10 results");
				System.out.println("------------------");
				search(terms);
				System.out.println("");
			} catch (ParseException e) {
				System.out.println("main/TableGenerator.java/generate() ParseException sending terms to search()");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("main/TableGenerator.java/generate() IOException sending terms to search()");
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("deprecation")
	public static void search(String input) throws ParseException, IOException{
        String query = input;
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
        Query q = new QueryParser(Version.LUCENE_41, "Desc", analyzer).parse(query);
        Directory index = new NIOFSDirectory(new File(INDEX_LOCATION));
        int hitsPerPage = 10;
        IndexReader reader = IndexReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
       
        System.out.println("Found " + hits.length + " hits.");
       
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            org.apache.lucene.document.Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("Disease") + " - " + d.get("Desc"));
        }
	}

}
