package jwm.ir.indexer;

import java.util.ArrayList;

public class TermPreprocessor {
	private String _term;
	private ArrayList<String> _stringsToReplace;
	public TermPreprocessor(ArrayList<String> stringsToReplace) {
		_stringsToReplace = stringsToReplace;
	}
	
	public void setTerm(String term) {
		_term = term;
	}
	public void cleanTerm() {
		
		
		for(String s : _stringsToReplace) {
			_term = _term.replace(s, "");
		}
			
		boolean hasDigit = _term.matches(".*\\d.*");
		boolean hasVowel = _term.matches(".*[aeiouy].*");
		boolean hasConsonant = _term.matches(".*[^aeiouy].*");
		
		if (!hasVowel || !hasConsonant || hasDigit || _term.length() == 0) {
			_term = null;
			return;
		}
		
		if (_term.length() <= 2 || _term.length() >= 20) {
			_term = null;
			return;
		}
		
	}
	
	public void stemTerm() {
		Stemmer stemmer = new Stemmer();
		stemmer.add(_term.toCharArray(), _term.length());
		stemmer.stem();
		_term = stemmer.toString();
	}
		
	public String getOutput() { return _term; }
}
