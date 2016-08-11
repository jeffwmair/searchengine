package thirdparty.jung;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-11.
 */
public class PageRankTest {

    @Test
    public void page_rank_basic_test() {

        DirectedSparseGraph<String, Integer> graph = new DirectedSparseGraph<>();
        final double TOLERANCE = 0.05;
        final double ALPHA = 0.15;
        final int MAX_ITERATIONS = 50;

        String[] verticies = { "a", "b", "c" };
        /*

         a -> b <-----> c
          \__________/

         */
        graph.addVertex(verticies[0]);
        graph.addVertex(verticies[1]);
        graph.addVertex(verticies[2]);
        graph.addEdge(1, "a", "b");
        graph.addEdge(2, "c", "b");
        graph.addEdge(3, "b", "c");
        graph.addEdge(4, "a", "c");

        PageRank<String, Integer> ranker = new PageRank<>(graph, ALPHA);
        ranker.setTolerance(TOLERANCE) ;
        ranker.setMaxIterations(MAX_ITERATIONS);

        ranker.evaluate();

        double almost_no_score = 0.05;
        double about_half_page_rank_score = 0.475;
        double difference_tolerance = 0.01;
        Assert.assertEquals("a has no in-links, so very low page-rank", almost_no_score, ranker.getVertexScore("a"), difference_tolerance);
        Assert.assertEquals("b has 2 in-links (just like c), so should have approximately 1/2 the entire page-rank of 1", about_half_page_rank_score, ranker.getVertexScore("b").doubleValue(), difference_tolerance);
        Assert.assertEquals("c has 2 in-links (just like b), so should have approximately 1/2 the entire page-rank of 1", about_half_page_rank_score, ranker.getVertexScore("c").doubleValue(), difference_tolerance);

        for (String verticy : verticies) {
            System.out.println(verticy + " = " + Double.toString(ranker.getVertexScore(verticy)));
        }
    }
}
