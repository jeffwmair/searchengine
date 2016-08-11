package jwm.ir.workers;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.service.Service;
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
	private final Service service;

	public PageRankCalculatorWorker(Db db, Service service) {
		this.service = service;
		_db = db;
		_graph = new DirectedSparseGraph<>();
	}

	@Override
	public void run() {
		calculatePageRanks();
	}

	private void calculatePageRanks() {

		log.info("beginning page-rank calculation");

		// get the verticies of the graph
		long start = System.currentTimeMillis();
		log.info("Beginning to get pageIds for calculating pageRank");

		log.warn("todo: fix this because its stupid");
		List<Page> pages = service.getAllPages();
		for(Page page : pages) {
			_graph.addVertex(Long.toString(page.getId()));
			log.debug("Page:"+page);
		}

		log.info("Finished getting pageIds for calculating pageRank (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)");


		// now get the edges (out links)
		int k = 0;
		int setSize = 500000;	// max size of sets of page ids to request links of
		int edgeNumber = 1;
		start = System.currentTimeMillis();
		log.info("Beginning to get page-links for calculating pageRank");
		boolean notDone = true;
//		while(notDone) {


			/**
			 *
			 * todo: don't need this loop anymore either
			 * Get Page objects back above, with link to PageLink collection (add to entity).
			 * Then we have everything here.

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
			 */

//			log.debug("Get pagelinks for:"+pageIdsToRequestLinks);
//			List<String> pageIdDestIds = _db.getPageLinks(pageIdsToRequestLinks);
//			log.debug("Got pagelinks:"+pageIdDestIds);
//			if (pageIdDestIds.isEmpty()) {
//				log.error("No pagelinks were found!!  Need to fix this");
//			}
//			for (String pageIdDestId : pageIdDestIds) {
//				String[] page_dest = pageIdDestId.split(",");
//				_graph.addEdge(edgeNumber++, page_dest[0], page_dest[1]);
//			}
		for(Page p : pages) {
			for (PageLink edge : p.getPageLinks()) {
				String edgeStart = Long.toString(edge.getPage().getId());
				String edgeEnd = Long.toString(edge.getDestinationPage().getId());
				log.debug("Adding edge.  Start:'"+edgeStart+"', End:'"+edgeEnd+"'");
				_graph.addEdge(edgeNumber++, edgeStart, edgeEnd);
			}
		}

//			k += setSize;
//		}
		log.info("Finished getting page-links for calculating pageRank  (" + ((System.currentTimeMillis() - start) / 1000.0) + "s)");

		// calculate PR
		log.info("Beginning to calculate PageRank on " + pages.size() + " pages");
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
		for(int i = 1; i <= pages.size(); i++) {
			String pageId = Long.toString(pages.get(i-1).getId());
			double pr = ranker.getVertexScore(pageId);
			log.debug("Got vector score for page '"+pageId+"':"+pr);
			pageRanks.put(Integer.parseInt(pageId), pr);
			if (i % maxPrsToSendAtOnce == 0) {
				// send 'em
				_db.updatePageRanks(pageRanks);
				log.info("A batch of PageRanks was sent to the Database");
				pageRanks.clear();
			}
			else {
				log.debug("not sending to db yet...");
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
		PageRank<String, Integer> ranker = new PageRank<>(_graph, ALPHA);
		ranker.setTolerance(TOLERANCE) ;
		ranker.setMaxIterations(MAX_ITERATIONS);

		ranker.evaluate();
		log.info("PageRank computed in " + (System.currentTimeMillis()-start) + " ms");

		for (String verticy : verticies) {
			log.info(verticy + " = " + Double.toString(ranker.getVertexScore(verticy)));
		}
	}
}
