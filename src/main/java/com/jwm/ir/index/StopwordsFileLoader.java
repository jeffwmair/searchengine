package com.jwm.ir.index;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-28.
 */
public class StopwordsFileLoader {

    final private static Logger log = LogManager.getLogger(StopwordsFileLoader.class);
    private final String filename;

    public StopwordsFileLoader(String filename) {
        this.filename = filename;
    }

    public List<String> getStopwordsFromFile() {
        log.debug("Loading stopwords from '"+filename+"'");
        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            log.error("Could not find stopwords file");
            return new ArrayList<>();
        }

        BufferedReader br = null;
        ArrayList<String> stopwords = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.add(line.toLowerCase());
            }
            br.close();
            return stopwords;
        } catch (Exception e) {
            log.error("Error loading stopwords file");
            return new ArrayList<>();
        }
    }

}
