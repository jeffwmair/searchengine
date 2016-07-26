package integration;

import org.junit.Test;

/**
 * Created by Jeff on 2016-07-19.
 */
public class IndexPagesIntegrationTest extends IntegrationTestBase {

    @Test
    public void indexThreePagesTest() {

        // arrange
        setupDb();
//        deployPhpServicesToApache();
//        deployWebPagesToBeIndexed();
//        deployRobotsTxtFile(RobotTxtState.Accept);
        startProgram(RunCrawler.Yes, RunIndexer.Yes);

        // assert


    }

    public void doNotIndexPagesBecauseRobotTxtRestrictsItTest() {

        // arrange
        setupDb();
        deployPhpServicesToApache();
        deployWebPagesToBeIndexed();
        deployRobotsTxtFile(RobotTxtState.Deny);

        // act
        startProgram(RunCrawler.Yes, RunIndexer.Yes);

        // asserts
    }




}
