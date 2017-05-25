package app.plugin;

import app.parse.Parser;
import app.request.JsoupRequester;
import app.request.Requester;
import org.junit.Before;
import org.mockito.Mockito;

public class HtmlCommentsTest {

  private HtmlComments plugin;
  private Parser parser;
  private Requester requester;

  @Before
  public void setUp() throws Exception {
    parser = Mockito.mock(Parser.class);
    requester = Mockito.mock(JsoupRequester.class);
    plugin = new HtmlComments(requester, parser);
  }

}
