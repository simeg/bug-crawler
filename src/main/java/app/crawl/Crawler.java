package app.crawl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Crawler implements Runnable {

  private static final Logger LOG = Logger.getLogger(Crawler.class.getName());

  /*
   * TODO for class:
   * - If any URL on URL-queue, consume it
   * - Put any sub-links found on queue
   * - Pass URL content to next HTML-queue
   */

  @Override
  public void run() {

  }

  public void crawl(Object url) throws IOException {

    LOG.log(Level.INFO, "Will crawl URL: {0}", url.toString());

    /*URL url = new URL(urlString);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(url.openStream()));

    StringBuilder sb = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null)
      sb.append(sanitizeHtml(inputLine) + " ");
    in.close();

    List<String> elementList = Arrays.asList(sb.toString().split(" "));

    List<String> hyperlinks = elementList.stream()
        .collect(Collectors.toList());

    return elementList;*/
  }

}
