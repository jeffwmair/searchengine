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

		List<Page> pages = service.getAllPages();
		int edgeNumber = 1;
		for(Page page : pages) {
			_graph.addVertex(Long.toString(page.getId()));
			for (PageLink edge : page.getPageLinks()) {
				String edgeStart = Long.toString(edge.getPage().getId());
				String edgeEnd = Long.toString(edge.getDestinationPage().getId());
				_graph.addEdge(edgeNumber++, edgeStart, edgeEnd);
			}
		}

		// calculate PR
		start = System.currentTimeMillis() ;
		PageRank<String, Integer> ranker = new PageRank<>(_graph, ALPHA);
		ranker.setTolerance(TOLERANCE) ;
		ranker.setMaxIterations(MAX_ITERATIONS);
		ranker.evaluate();
		log.info("Finished calculating PageRank in " + (System.currentTimeMillis()-start) / 1000.0 + "s on "+pages.size()+" pages");

		double prSum = 0.0;
		HashMap<Integer, Double> pageRanks = new HashMap<>();
		for(int i = 1; i <= pages.size(); i++) {
			String pageId = Long.toString(pages.get(i-1).getId());
			double pr = ranker.getVertexScore(pageId);
			log.debug("Got vector score for page '"+pageId+"':"+pr);
			pageRanks.put(Integer.parseInt(pageId), pr);
			prSum += pr;
		}

		_db.updatePageRanks(pageRanks);
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
