package integration;

/**
 * Created by Jeff on 2016-07-19.
 */
abstract class IntegrationTestBase {

    enum IntegrationDbScript { Standard };
    enum RunCrawler { Yes, No };
    enum RunIndexer { Yes, No };
    enum RobotTxtState { Deny, Accept };

    void setupDb(IntegrationDbScript script) {
        throw new RuntimeException("Not implemented");
    }

    void deployPhpServicesToApache() {
        throw new RuntimeException("Not impl");
    }

    void deployRobotsTxtFile(RobotTxtState state) {
        throw new RuntimeException("Not impl");
    }

    void deployWebPagesToBeIndexed() {
        throw new RuntimeException("Not impl");
    }

    void startProgram(RunCrawler runCrawler, RunIndexer runIndexer) {
        throw new RuntimeException("Not impl");
    }
}
