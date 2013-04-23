package indexers;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class NLH {
	
	/* 
	 * Retrieves each NLH chapter and indexes diseases, adding relevant
	 * attributes to the Lucene files. (Name and chapter, general info and symptoms)
	 */
	
	public static final String INDEX_LOCATION = "data/nlh_index";
	
	public void indexNLH(){
		
		//Lists all files in data/nlh directory
		String path = "./data/nlh";
		File file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		//For each file, do something. Here will will send to HTML stripper
		//which also adds data to Lucene files.
		 
		for(int i=0; i < 1; i++){
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i];
				
				try {
					extractText(file);
				} catch (IOException e) {
					System.out.println("Error in NLH.java/indexNLH - IO error sending file to extractText");
					e.printStackTrace();
				}

			}
		}
		
		//For testing, searches Lucene files for relevant matches.
		try {
			search("Vesikler");
		} catch (ParseException e) {
			System.out.println("Error in NLH.java/indexNLH - parse error sending string to search()");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error in NLH.java/indexNLH - IO error sending string to search()");
			e.printStackTrace();
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
		
		Elements h_3 = doc.select("h3");
		Elements h_2 = doc.select("h2");
		String desc = "";
		
		if (h_3.size()>0){
			for(int i=0; i<h_3.size(); i++){
				Element e = h_3.get(i);
				
				//For each disease, create new lucene document and add disease name.
				System.out.println(e.ownText());
				org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
				d.add(new TextField("Disease", e.ownText(), Field.Store.YES));
				
								
				Element eSib = e.nextElementSibling();
				
				if(eSib.className().contains("revidert")){
					eSib = eSib.nextElementSibling();
				}
				for (Element element : eSib.select("h5")){
					if(element.ownText().contains("Generelt") || element.ownText().contains("Definisjon")){
						//System.out.println(element.ownText());	
						for (Element gElement : element.siblingElements()){
							if(gElement.select("p").hasText()){
								
								//For each sentence under 'Generelt' add new text line to disease data file.
								System.out.println(gElement.ownText());
								//d.add(new TextField("Desc", gElement.ownText(), Field.Store.YES));
								desc += gElement.ownText() + " ";
							}
						}
					}
					if(element.ownText().contains("Symptomer")){
						//System.out.println(element.ownText());
						for (Element sElement : element.siblingElements()){
							if(sElement.select("p").hasText()){
								
								//For each sentence under 'Symptomer' add new text line to disease data file.
								System.out.println(sElement.ownText());
								//d.add(new TextField("Desc", sElement.ownText(), Field.Store.YES));
								desc += sElement.ownText() + " ";
							}
						}
					}
				}
				
				//Writes added files to Lucene document, should do this for the current disease.
				d.add(new TextField("Desc", desc, Field.Store.YES));
				writer.addDocument(d);
				desc = "";
				eSib = eSib.nextElementSibling();
				System.out.println("---");
			}
		} else {
			for(int i=0; i<h_2.size(); i++){
				Element e = h_2.get(i);
				
				System.out.println(e.ownText());
				org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
				d.add(new TextField("disease", e.ownText(), Field.Store.YES));
								
				Element eSib = e.nextElementSibling();
				if(eSib.className().contains("revidert")){
					eSib = eSib.nextElementSibling();
				}
				while(eSib.nextElementSibling() != null){
					for (Element element : eSib.select("h5")){
						if(element.ownText().contains("Generelt") || element.ownText().contains("Definisjon")){
							//System.out.println(element.ownText());	
							for (Element gElement : element.siblingElements()){
								if(gElement.select("p").hasText()){
									System.out.println(gElement.ownText());
								}
							}
						}
						//TODO; Doesn't work if symptoms are in bullets
						if(element.ownText().contains("Symptomer")){
							//System.out.println(element.ownText());
							for (Element sElement : element.siblingElements()){
								if(sElement.select("p").hasText()){
									System.out.println(sElement.ownText());
								}
							}
						}
					}
				eSib = eSib.nextElementSibling();
				}
				System.out.println("---");
			}
		}
		
		//Close Lucene writer
		writer.close();
	}
	
	//Tester function for searching through Lucene files.
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
            System.out.println((i + 1) + ". " + d.get("Disease") + " with " + d.get("Desc"));
        }
}
}
