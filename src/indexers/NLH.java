package indexers;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import main.CaseParser;
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
import org.jsoup.select.Elements;


public class NLH {
	
	/* 
	 * Retrieves each NLH chapter and indexes diseases, adding relevant
	 * attributes to the Lucene files. (Name and chapter, general info and symptoms)
	 */
	
	public static final String INDEX_LOCATION = "data/nlh_index";
	
	public void indexNLH(){
		
		try {
			extractText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Fishes out wanted text from HTML file, prints to console for testing, creates Lucene files for each disease.
	public void extractText() throws IOException{
		
		//Lists all files in data/nlh directory
		String path = "./data/nlh";
		File file = null;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		//Initialize Lucene index, analyze and writer
		Directory index = new NIOFSDirectory(new File(INDEX_LOCATION));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		IndexWriter writer = new IndexWriter(index, config);
		
		//For each file, do something. Here will will send to HTML stripper
		//which also adds data to Lucene files. 
		for(int i=0; i < listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				file = listOfFiles[i];
			}
			
			//Formats HTML document with Jsoup
			Document doc = Jsoup.parse(file, "ISO-8859-1", "");
			
			Elements h_3 = doc.select("h3");
			Elements h_2 = doc.select("h2");
			String desc = "";
			
			if (h_3.size()>0){
				for(int k=0; k<h_3.size(); k++){
					Element e = h_3.get(k);
					
					//For each disease, create new Lucene document and add disease name.
					//System.out.println(e.ownText());
					org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
					d.add(new TextField("Disease", e.ownText(), Field.Store.YES));
					
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
										
										//For each sentence under 'Generelt' add new text line to disease data file.
										//System.out.println(gElement.ownText());
										desc += gElement.ownText() + " ";
									}
								}
							}
							if(element.ownText().contains("Symptomer")){
								//System.out.println(element.ownText());
								for (Element sElement : element.siblingElements()){
									if(sElement.select("p").hasText()){
										
										//For each sentence under 'Symptomer' add new text line to disease data file.
										//System.out.println(sElement.ownText());
										desc += sElement.ownText() + " ";
									}
								}
							}
						}
						
						eSib = eSib.nextElementSibling();
					}
					//Strips/stems description for easier searching.
					Scanner scanner = new Scanner(desc);
					String parsedString = "";
					while(scanner.hasNext()){
						String word = scanner.next();
						word = CaseParser.cleanWord(word);
						word = word.toLowerCase();
						
						if(CaseParser.isStopWord(word)){
							continue;
						}
						word = CaseParser.stemming(word);
						parsedString += word;
						if(scanner.hasNext()){
							parsedString += " ";
						}
					}
					
					//Writes added files to Lucene document, should do this for the current disease.
					d.add(new TextField("Desc", parsedString, Field.Store.YES));
					writer.addDocument(d);
					parsedString = "";
					desc = "";
					//System.out.println("---");
				}
			} else {
				for(int j=0; j<h_2.size(); j++){
					Element e = h_2.get(j);
					
					//System.out.println(e.ownText());
					org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
					d.add(new TextField("Disease", e.ownText(), Field.Store.YES));
									
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
										//System.out.println(gElement.ownText());
										desc += gElement.ownText() + " ";
									}
								}
							}
							//TODO; Doesn't work if symptoms are in bullets
							if(element.ownText().contains("Symptomer")){
								//System.out.println(element.ownText());
								for (Element sElement : element.siblingElements()){
									if(sElement.select("p").hasText()){
										//System.out.println(sElement.ownText());
										desc += sElement.ownText() + " ";
									}
								}
							}
						}
						
						eSib = eSib.nextElementSibling();
					}
					//Strips/stems description for easier searching.
					Scanner scanner = new Scanner(desc);
					String parsedString = "";
					while(scanner.hasNext()){
						String word = scanner.next();
						word = CaseParser.cleanWord(word);
						word = word.toLowerCase();
						
						if(CaseParser.isStopWord(word)){
							continue;
						}
						word = CaseParser.stemming(word);
						parsedString += word;
						if(scanner.hasNext()){
							parsedString += " ";
						}
					}
					//Writes added files to Lucene document, should do this for the current disease.
					d.add(new TextField("Desc", parsedString, Field.Store.YES));
					writer.addDocument(d);
					parsedString = "";
					desc = "";
					//System.out.println("---");
				}
			}
		
		}
		//Close Lucene writer
		writer.close();
	}
}
