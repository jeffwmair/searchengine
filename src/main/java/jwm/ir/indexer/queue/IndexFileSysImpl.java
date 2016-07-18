package jwm.ir.indexer.queue;

import jwm.ir.indexer.ParsedWebPage;
import jwm.ir.indexer.ParsedWebPageImpl;
import jwm.ir.indexer.ParsedWebPageNoneImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * IndexFileSystem implementation that is responsible for reading & writing
 * web pages to be indexed to the disk.
 *
 * Created by Jeff on 2016-07-17.
 */
class IndexFileSysImpl implements IndexFileSys {

    final private static Logger log = LogManager.getLogger(IndexFileSysImpl.class);
    private final File documentDir;

    IndexFileSysImpl(String directory) {

        if (directory == null || directory.isEmpty()) throw new IllegalArgumentException("Must provide a valid directory");

        File documentDir = new File(directory);
        if (!documentDir.exists()) {
            log.info("Directory '"+directory+"' does not exist, so creating it.");
            documentDir.mkdir();
        }

        this.documentDir = documentDir;

    }

    /**
     * Write this parsed web page to disk as a file.
     * @param workerId
     * @param parsedWebPage
     */
    @Override
    public synchronized void writeToDisk(int workerId, ParsedWebPage parsedWebPage) {
	    Format formatter = new SimpleDateFormat("yyyy_M_dd HHmmssSSS");
	    String dateFormatted = formatter.format(new Date());

        try
        {
            String filenamePrefix = "crawler"+workerId;
        	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(documentDir.getAbsolutePath() + "/" + filenamePrefix + "_" + dateFormatted + ".txt"), "UTF-8"));
            out.write(parsedWebPage.getUrl() + "\n");
    		out.write(parsedWebPage.getPageContent().replace("\\\"", "\""));
            out.close();
		}
        catch (IOException e)
        {
        	log.error("Error writing url to file " + parsedWebPage.getUrl() + ":"+e.getMessage(), e);
		}
    }

    /**
     * Get the next file from disk to be indexed for this worker.
     * @param workerId
     * @return
     */
    @Override
    public synchronized ParsedWebPage readFromDiskAndDelete(int workerId) {

        ArrayList<File> files = getFilesForWorker(workerId);
        if (files.size() == 0) {
            return new ParsedWebPageNoneImpl();
        }

        // just grab the first file in the list
        File nextFile = files.get(0);

        String url = null, content = null;

        BufferedReader br = null;
        try {
            log.info("Beginning reading the file "+nextFile.getName());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(nextFile.getAbsolutePath()), "UTF-8"));

            // first line is the url
            url = br.readLine();

            // remainder of the file is the content of the web page
            String line;
            StringBuilder contentSb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                contentSb.append(line);
			}

			br.close();
            content = contentSb.toString();
			log.info("Finished reading the file "+nextFile.getName());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String fname = nextFile.getName();
            boolean deleted = nextFile.delete();
            log.debug("File '"+fname+"' deleted after reading.  Delete operation success:"+deleted);

        }

        return new ParsedWebPageImpl(url, content);
    }


    /**
     * Get a count of files on disk to be indexed for this worker.
     * @param workerId
     * @return
     */
    @Override
    public synchronized int countFiles(int workerId) {
        return getFilesForWorker(workerId).size();
    }

    private ArrayList<File> getFilesForWorker(int workerId) {
        File[] files = documentDir.listFiles();
        ArrayList<File> filesList = new ArrayList<>();
        for(File f : files) {
            if (f.getName().startsWith("crawler" + workerId + "_")) {
                filesList.add(f);
            }
        }
        return filesList;
    }
}
