package jwm.ir.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jwm.ir.utils.Database;
import jwm.ir.utils.Log;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;


public class PageRankCalculatorWorker implements Runnable {

	Database _db;
	Log _log;
	DirectedSparseGraph<String, Integer> _graph = null;
	
	final double TOLERANCE = 0.05;
	final double ALPHA = 0.15;
	final int MAX_ITERATIONS = 50;
	final String CLIENT_NAME = "PageRankWorker";
	
	public PageRankCalculatorWorker(Database db, Log log) {
		_db = db;
		_log = log;
		_graph = new DirectedSparseGraph<String, Integer>();
	}
	
	@Override
	public void run() {
		
//		 makeSampleGraph();
		 calculatePageRanks();
	}
		
	private void calculatePageRanks() {
		
		int maxPageIdsToGet = 50000;
		boolean notDone = true;
		String lastReceivedPageId = "0";
		 
		// get the verticies of the graph
		String[] jsonPageIds = null;
		ArrayList<String> allPageIds = new ArrayList<String>();
		long start = System.currentTimeMillis();
		_log.LogMessage(CLIENT_NAME, "Beginning to get pageIds for calculating pageRank", false);
		while(notDone) {
			 
			 jsonPageIds = _db.getPageIdsGreaterThanPageId(lastReceivedPageId, maxPageIdsToGet, CLIENT_NAME, _log);
			 
			 for(String id : jsonPageIds) {
				 allPageIds.add(id);
				 _graph.addVertex(id);
			 }
			 
			 if (jsonPageIds.length == 0) {
				 notDone = false;
			 }
			 else {
				 lastReceivedPageId = jsonPageIds[jsonPageIds.length-1];	 
			 }
		 }
		 _log.LogMessage(CLIENT_NAME, "Finished getting pageIds for calculating pageRank (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)", false);
		 
		 notDone = true;
		 		 
		 // now get the edges (out links)
		 int k = 0;
		 int setSize = 500000;	// max size of sets of page ids to request links of
		 int edgeNumber = 1;
		 start = System.currentTimeMillis();
		 _log.LogMessage(CLIENT_NAME, "Beginning to get page-links for calculating pageRank", false);
		 while(notDone) {
			 
			 ArrayList<String> pageIdsToRequestLinks = new ArrayList<String>();
			 for(int j = 0; j < setSize; j++) {
				 if (j+k >= allPageIds.size()-1) {
					 notDone = false;
					 break;
				 }
				 else {
					 pageIdsToRequestLinks.add(allPageIds.get(k+j));	 
				 }	 
			 }
			 
			 ArrayList<String> pageIdDestIds = _db.getPageLinks(pageIdsToRequestLinks, CLIENT_NAME, _log);
			 for(int j = 0; j < pageIdDestIds.size(); j++) {
				 String[] page_dest = pageIdDestIds.get(j).split(",");
				 _graph.addEdge(edgeNumber++, page_dest[0], page_dest[1]);
			 }
			 
			 k += setSize;
		 }	
		 _log.LogMessage(CLIENT_NAME, "Finished getting page-links for calculating pageRank  (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)", false);
		 
		 // calculate PR
		 _log.LogMessage(CLIENT_NAME, "Beginning to calculate PageRank on " + allPageIds.size() + " pages", false);
		 start = System.currentTimeMillis() ;
		 PageRank<String, Integer> ranker = new PageRank<String, Integer>(_graph, ALPHA);
		 ranker.setTolerance(TOLERANCE) ;
		 ranker.setMaxIterations(MAX_ITERATIONS);
		 ranker.evaluate();
		 _log.LogMessage(CLIENT_NAME, "Finished calculating PageRank in " + (System.currentTimeMillis()-start) / 1000.0 + "s", false);
		 
		 double prSum = 0.0;
		 int maxPrsToSendAtOnce = 50000;
		 _log.LogMessage(CLIENT_NAME, "Beginning to send PageRanks to the Database", false);
		 HashMap<Integer, Double> pageRanks = new HashMap<Integer, Double>();
		 for(int i = 1; i <= allPageIds.size(); i++) {
			 String pageId = allPageIds.get(i-1);
			 double pr = ranker.getVertexScore(pageId);
			 pageRanks.put(Integer.parseInt(pageId), pr);
			 if (i % maxPrsToSendAtOnce == 0) {
				 // send 'em
				 _db.updatePageRanks(pageRanks, CLIENT_NAME, _log);
				 _log.LogMessage(CLIENT_NAME, "A batch of PageRanks was sent to the Database", false);
				 pageRanks.clear();
			 }
			 prSum += pr;
		 }
		 
		 _db.updatePageRanks(pageRanks, CLIENT_NAME, _log);				 
		 pageRanks.clear();		 
		 _log.LogMessage(CLIENT_NAME, "Finished calculating PageRank on all pages; sum of all PR scores was: " + prSum, false);
	}
	
	private void makeSampleGraph() {
		String[] verticies = { "a", "b", "c" };
		_graph.addVertex(verticies[0]);
		_graph.addVertex(verticies[1]);
		_graph.addVertex(verticies[2]);
		_graph.addEdge(1, "a", "b");
		_graph.addEdge(2, "c", "b");
		_graph.addEdge(3, "b", "c");
		_graph.addEdge(4, "a", "c");
		
		long start = System.currentTimeMillis() ;
		PageRank<String, Integer> ranker = new PageRank<String, Integer>(_graph, ALPHA);
		ranker.setTolerance(TOLERANCE) ;
		ranker.setMaxIterations(MAX_ITERATIONS);

		ranker.evaluate();
		_log.LogMessage(CLIENT_NAME, "PageRank computed in " + (System.currentTimeMillis()-start) + " ms", false);
       
		for(int i = 0; i < verticies.length; i++) {
			_log.LogMessage(CLIENT_NAME, verticies[i] + " = " + Double.toString(ranker.getVertexScore(verticies[i])), false);	 
		}  
	}	
}
