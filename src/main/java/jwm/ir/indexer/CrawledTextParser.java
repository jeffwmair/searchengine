package jwm.ir.indexer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class CrawledTextParser {

	final private static Logger log = LogManager.getLogger(CrawledTextParser.class);
	private List<String> _stopwords;
	private boolean _useStemming;
	private boolean _useStopwords;
	private HashMap<String, Integer> _termFrequencies;
	private TermPreprocessor _tp;
	public CrawledTextParser(boolean useStemming,
			boolean useStopwords, 
			List<String> stopwords,
			TermPreprocessor tp) {
		_useStemming = useStemming;
		_useStopwords = useStopwords;
		_stopwords = stopwords;
		_termFrequencies = new HashMap<>();
		_tp = tp;
	}
	
	public void processInput(String line) {

		if (line == null) return;
		
		/* split the line with spaces between words -- seems safe to assume */
		String terms[] = line.trim().toLowerCase().split("\\s+"); 
		
		/* we just care about the title, abstract, and authors for term extraction */
		for(int i = 0; i < terms.length; i++) {
			
			String term = null;
			try{
				term = terms[i].trim();
				_tp.setTerm(term);
		
				/* PRE-PROCESSING: Jeff - cleanup of punctuation */
				_tp.cleanTerm();
				if (_tp.getOutput() == null) continue;
				
				
				/* PRE-PROCESSING: filter out stopwords */
				if (_useStopwords && _stopwords.contains(_tp.getOutput())) {
					continue;	// skip this term
				}
				
				/* PRE-PROCESSING: stemming */
				if (_useStemming) {
					_tp.stemTerm();
				}
				
				Integer tf = _termFrequencies.get(_tp.getOutput());
				if (tf == null) {
					tf = 0;
				}
				
				String t = _tp.getOutput();			
				_termFrequencies.put(_tp.getOutput(), ++tf);
			}
			catch(Exception e) {
				log.error("Error parsing '"+term+"': " + e.toString());
			}

		}
			
	}

	public HashMap<String, Integer> getTermFrequencies() { return _termFrequencies; }	
	
	
}
