package indexers;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class NLH {
	public static final String INDEX_LOCATION = "/nlh_index";
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
		for(int i=0; i < 1; i++){
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i];
				
				try {
					extractText(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	
	//Strips HTML tags from file, currently prints to console
	public void extractText(File f) throws IOException{
		
		//Initialize Lucene index, analyze and writer
		Directory index = new NIOFSDirectory(new File(INDEX_LOCATION));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		IndexWriter writer = new IndexWriter(index, config);
		
		//Strips HTML document with Jsoup
		Document doc = Jsoup.parse(f, "ISO-8859-1", "");
		//Each element with the selected classes gets pulled
		for( Element element : doc.select("p, h3, h5") )
		{
			if( element.text().contains("Publisert") ) { continue; }
			
			//If element has Header 3 attribute it is a new disease, create new Lucene document
			if( element.hasAttr("h3") ){
				org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
				d.add(new TextField("disease", element.toString(), Field.Store.YES));
			}
			
		    System.out.println(element.text());
		}
		
		//Close Lucene writer
		writer.close();

	}
}
