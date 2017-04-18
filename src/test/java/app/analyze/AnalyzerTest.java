package app.analyze;

import app.parse.Parser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AnalyzerTest {

  private Analyzer analyzer;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    final Config conf = ConfigFactory.load();
    final List<Object> paths = conf.getList("analyzer.testFilePaths").unwrapped();

    parser = Mockito.mock(Parser.class);
    analyzer = new Analyzer(parser, paths);
  }

  @Test
  public void testGetFileBugsMatchingHtmlHashes() throws Exception {
    when(parser.getResponseStatusCode(any(String.class))).thenReturn(200);
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com")).thenReturn(100);

    Assert.assertEquals(Collections.emptySet(), analyzer.getFileBugs("http://specific-domain.com"));
  }

  @Test
  public void testGetFileBugs404Response() throws Exception {
    // 404 occurs when website is loaded but no content on website
    when(parser.getResponseStatusCode(any(String.class))).thenReturn(404);
    // Hashes do not match => 404 response rendered different HTML
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com")).thenReturn(101);

    Assert.assertEquals(Collections.emptySet(), analyzer.getFileBugs("http://specific-domain.com"));
  }

  @Test
  public void testGetFileBugsThrowException() throws Exception {
    // Exception occurs when website is unable to load
    when(parser.getResponseStatusCode(any(String.class))).thenThrow(new IOException());

    Assert.assertEquals(Collections.emptySet(), analyzer.getFileBugs("http://specific-domain.com"));
  }

  @Test
  public void testIsWordpress() throws Exception {
    // Happy
    final String url = "http://irrelevant-name.com";
    final String wpWebsite = url + "/wp-login.php";

    when(parser.getResponseStatusCode(wpWebsite)).thenReturn(200);
    when(parser.getHtmlHash(url)).thenReturn(100);
    when(parser.getHtmlHash(wpWebsite)).thenReturn(101);

    Assert.assertTrue(analyzer.isWordpress(url));

    // Sad
    final String url2 = "http://irrelevant-name.com";
    final String wpWebsite2 = url2 + "/wp-login.php";

    when(parser.getResponseStatusCode(wpWebsite2)).thenReturn(200);
    when(parser.getHtmlHash(url2)).thenReturn(100);
    when(parser.getHtmlHash(wpWebsite2)).thenReturn(100);

    Assert.assertFalse(analyzer.isWordpress(url2));
  }

  @Test
  public void testGetFileBugs() throws Exception {
    when(parser.getResponseStatusCode("http://specific-domain.com/filePath1")).thenReturn(200);
    when(parser.getResponseStatusCode("http://specific-domain.com/filePath2")).thenReturn(200);
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com/filePath1")).thenReturn(101);
    when(parser.getHtmlHash("http://specific-domain.com/filePath2")).thenReturn(102);

    final Bug bug1 = Bug.create(
        Bug.BugType.FILE_ACCESS,
        "http://specific-domain.com/filePath1",
        "Access to filePath1",
        Optional.of("http://specific-domain.com/filePath1"));

    final Bug bug2 = Bug.create(
        Bug.BugType.FILE_ACCESS,
        "http://specific-domain.com/filePath2",
        "Access to filePath2",
        Optional.of("http://specific-domain.com/filePath2"));

    final LinkedHashSet<Bug> expectedBugs = new LinkedHashSet<>(Arrays.asList(bug1, bug2));
    final Set<Bug> actualBugs = analyzer.getFileBugs("http://specific-domain.com");

    Assert.assertEquals(expectedBugs.size(), actualBugs.size());
    Assert.assertEquals(expectedBugs.toString(), actualBugs.toString());
  }

}
