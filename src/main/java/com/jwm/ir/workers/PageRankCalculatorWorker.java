package com.jwm.ir.workers;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import com.jwm.ir.entity.Page;
import com.jwm.ir.entity.PageLink;
import com.jwm.ir.service.Service;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class PageRankCalculatorWorker implements Runnable {

	private final static Logger log = LogManager.getLogger(PageRankCalculatorWorker.class);
	private final DirectedSparseGraph<Long, Integer> graph;
	private final double TOLERANCE = 0.05;
	private final double ALPHA = 0.15;
	private final int MAX_ITERATIONS = 50;
	private final Service service;

	public PageRankCalculatorWorker(Service service) {
		this.service = service;
		graph = new DirectedSparseGraph<>();
	}

	@Override
	public void run() {
		calculatePageRanks();
	}

	private void calculatePageRanks() {

		log.info("beginning page-rank calculation");
		List<Page> pages = service.getPages(Service.FilterVerified.VerifiedOnly);

		// construct our graph of vertices and edges
		int edgeNumber = 1;
		for(Page vertex : pages) {
			graph.addVertex(vertex.getId());
			for (PageLink edge : vertex.getPageLinks()) {
				graph.addEdge(edgeNumber++, edge.getSourcePageId(), edge.getDestinationPageId());
			}
		}

		// calculate PR
		long start = System.currentTimeMillis() ;
		PageRank<Long, Integer> ranker = new PageRank<>(graph, ALPHA);
		ranker.setTolerance(TOLERANCE) ;
		ranker.setMaxIterations(MAX_ITERATIONS);
		ranker.evaluate();
		log.info("Finished calculating PageRank in " + (System.currentTimeMillis()-start) / 1000.0 + "s on "+pages.size()+" pages");

		/**
		 * extract the page-ranks here rather than passing around the PageRank api elsewhere in the application
		 */
		double prSum = 0.0;
		Map<Long, Double> pageRanks = new HashMap<>();
		for(Page p : pages) {
			double pr = ranker.getVertexScore(p.getId());
			log.debug("Got vector score for page '"+p+"':"+pr);
			pageRanks.put(p.getId(), pr);
			prSum += pr;
		}

		service.updatePageRanks(pageRanks);
		log.info("Finished calculating PageRank on all pages; sum of all PR scores was: " + prSum);
	}
}
