package jwm.ir.crawler;

import jwm.ir.message.WebResource;
import jwm.ir.message.WebResourcePageImpl;
import jwm.ir.utils.StringBuilderWithNewline;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Jeff on 2016-07-20.
 */
public class ResourceFetcher {

	final private static Logger log = LogManager.getLogger(ResourceFetcher.class);

	public ResourceFetcher() {


	}

	/**
	 * Get a ParsedWebPage instance
	 * @return
	 */
	public WebResource getWebResource(String urlString) {

		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;
		StringBuilder sb = new StringBuilder();

		try {
			url = new URL(urlString);

			/**
			 * set user-agent, timeout, etc?
			 *
			 *	.timeout(10000)
				.header("Accept-Charset", "UTF-8")
				.header("user-agent", "JeffMairFriendlyWebCrawler")

			 */
			is = url.openStream();  // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));

			while ((line = br.readLine()) != null) {
				if (!sb.toString().isEmpty()) {
					sb.append("\n");
				}
				sb.append(line);
			}

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}

		return new WebResourcePageImpl(urlString, sb.toString());
	}

}
