package app.plugin;

import app.parse.Parser;
import app.request.Requester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PageFinderTest {

  private PageFinder plugin;
  private Parser parser;
  private Requester requester;

  @Before
  public void setUp() throws Exception {
    parser = Mockito.mock(Parser.class);
    requester = Mockito.mock(Requester.class);
    plugin = new PageFinder(requester, parser);
  }

  @Test
  public void testGetFileBugsMatchingHtmlHashes() throws Exception {
    /*when(parser.getResponseStatusCode(any(String.class))).thenReturn(200);
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com")).thenReturn(100);

    assertEquals(Collections.emptySet(), plugin.inspect("http://specific-domain.com"));*/
  }

  @Test
  public void testGetFileBugs404Response() throws Exception {
    /*// 404 occurs when website is loaded but no content on website
    when(parser.getResponseStatusCode(any(String.class))).thenReturn(404);
    // Hashes do not match => 404 response rendered different HTML
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com")).thenReturn(101);

    assertEquals(Collections.emptySet(), plugin.inspect("http://specific-domain.com"));*/
  }

  @Test
  public void testGetFileBugsThrowException() throws Exception {
    /*// Exception occurs when website is unable to load
    when(parser.getResponseStatusCode(any(String.class))).thenThrow(new IOException());

    assertEquals(Collections.emptySet(), plugin.inspect("http://specific-domain.com"));*/
  }

  @Test
  public void testGetFileBugs() throws Exception {
    /*when(parser.getResponseStatusCode("http://specific-domain.com/phpinfo.php")).thenReturn(200);
    when(parser.getResponseStatusCode("http://specific-domain.com/phpmyadmin")).thenReturn(200);
    when(parser.getHtmlHash(any(String.class))).thenReturn(100);
    when(parser.getHtmlHash("http://specific-domain.com/phpinfo.php")).thenReturn(101);
    when(parser.getHtmlHash("http://specific-domain.com/phpmyadmin")).thenReturn(102);

    final Bug bug1 = Bug.create(
        Bug.BugType.FILE_ACCESS,
        "http://specific-domain.com/phpinfo.php",
        "Access to phpinfo.php",
        Optional.of("http://specific-domain.com/phpinfo.php"));

    final Bug bug2 = Bug.create(
        Bug.BugType.FILE_ACCESS,
        "http://specific-domain.com/phpmyadmin",
        "Access to phpmyadmin",
        Optional.of("http://specific-domain.com/phpmyadmin"));

    final Set<Bug> expectedBugs = Sets.newHashSet(bug1, bug2);
    final Set<Bug> actualBugs = plugin.inspect("http://specific-domain.com");

    assertEquals(expectedBugs.size(), actualBugs.size());*/
  }
}
