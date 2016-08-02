package jwm.ir.workers;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import jwm.ir.utils.Db;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class PageRankCalculatorWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(PageRankCalculatorWorker.class);
	private Db _db;
	private DirectedSparseGraph<String, Integer> _graph = null;
	
	private final double TOLERANCE = 0.05;
	private final double ALPHA = 0.15;
	private final int MAX_ITERATIONS = 50;

	public PageRankCalculatorWorker(Db db) {
		_db = db;
		_graph = new DirectedSparseGraph<>();
	}
	
	@Override
	public void run() {
		 calculatePageRanks();
	}
		
	private void calculatePageRanks() {
		
		int maxPageIdsToGet = 50000;
		boolean notDone = true;
		String lastReceivedPageId = "0";
		 
		// get the verticies of the graph
		String[] jsonPageIds;
		ArrayList<String> allPageIds = new ArrayList<>();
		long start = System.currentTimeMillis();
		log.info("Beginning to get pageIds for calculating pageRank");
		while(notDone) {
			 
			 jsonPageIds = _db.getPageIdsGreaterThanPageId(lastReceivedPageId, maxPageIdsToGet);
			 
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
		 log.info("Finished getting pageIds for calculating pageRank (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)");
		 
		 notDone = true;
		 		 
		 // now get the edges (out links)
		 int k = 0;
		 int setSize = 500000;	// max size of sets of page ids to request links of
		 int edgeNumber = 1;
		 start = System.currentTimeMillis();
		 log.info("Beginning to get page-links for calculating pageRank");
		 while(notDone) {
			 
			 ArrayList<String> pageIdsToRequestLinks = new ArrayList<>();
			 for(int j = 0; j < setSize; j++) {
				 if (j+k >= allPageIds.size()-1) {
					 notDone = false;
					 break;
				 }
				 else {
					 pageIdsToRequestLinks.add(allPageIds.get(k+j));	 
				 }	 
			 }
			 
			 List<String> pageIdDestIds = _db.getPageLinks(pageIdsToRequestLinks);
			 for(int j = 0; j < pageIdDestIds.size(); j++) {
				 String[] page_dest = pageIdDestIds.get(j).split(",");
				 _graph.addEdge(edgeNumber++, page_dest[0], page_dest[1]);
			 }
			 
			 k += setSize;
		 }	
		 log.info("Finished getting page-links for calculating pageRank  (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)");
		 
		 // calculate PR
		 log.info("Beginning to calculate PageRank on " + allPageIds.size() + " pages");
		 start = System.currentTimeMillis() ;
		 PageRank<String, Integer> ranker = new PageRank<>(_graph, ALPHA);
		 ranker.setTolerance(TOLERANCE) ;
		 ranker.setMaxIterations(MAX_ITERATIONS);
		 ranker.evaluate();
		 log.info("Finished calculating PageRank in " + (System.currentTimeMillis()-start) / 1000.0 + "s");
		 
		 double prSum = 0.0;
		 int maxPrsToSendAtOnce = 50000;
		 log.info("Beginning to send PageRanks to the Database");
		 HashMap<Integer, Double> pageRanks = new HashMap<>();
		 for(int i = 1; i <= allPageIds.size(); i++) {
			 String pageId = allPageIds.get(i-1);
			 double pr = ranker.getVertexScore(pageId);
			 pageRanks.put(Integer.parseInt(pageId), pr);
			 if (i % maxPrsToSendAtOnce == 0) {
				 // send 'em
				 _db.updatePageRanks(pageRanks);
				 log.info("A batch of PageRanks was sent to the Database");
				 pageRanks.clear();
			 }
			 prSum += pr;
		 }
		 
		 _db.updatePageRanks(pageRanks);
		 pageRanks.clear();		 
		 log.info("Finished calculating PageRank on all pages; sum of all PR scores was: " + prSum);
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
		log.info("PageRank computed in " + (System.currentTimeMillis()-start) + " ms");
       
		for(int i = 0; i < verticies.length; i++) {
			log.info(verticies[i] + " = " + Double.toString(ranker.getVertexScore(verticies[i])));
		}  
	}	
}
