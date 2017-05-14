package app.plugin;

import app.parse.Parser;
import org.junit.Before;
import org.mockito.Mockito;

public class HtmlCommentsTest {

  private HtmlComments plugin;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    parser = Mockito.mock(Parser.class);
    plugin = new HtmlComments(parser);
  }

}
