package app.analyze;

import app.parse.Parser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AnalyzerTest {

  private Analyzer analyzer;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    final Config conf = ConfigFactory.load();
    List<Object> paths = conf.getList("analyzer.testFilePaths").unwrapped();

    this.parser = Mockito.mock(Parser.class);
    this.analyzer = new Analyzer(parser, paths);
  }

  @Test
  public void testAnalyze404Response() throws Exception {
    // 404 occurs when website is loaded but no content on website
    when(this.parser.getResponseStatusCode(any(String.class))).thenReturn(404);

    Assert.assertEquals(Sets.newHashSet(), this.analyzer.analyze("http://specific-domain.com"));
  }

  @Test
  public void testAnalyzeThrowException() throws Exception {
    // Exception occurs when website is unable to load
    when(this.parser.getResponseStatusCode(any(String.class))).thenThrow(new IOException());

    Assert.assertEquals(Sets.newHashSet(), this.analyzer.analyze("http://specific-domain.com"));
  }

  @Test
  public void testAnalyze() throws Exception {
    when(this.parser.getResponseStatusCode("http://specific-domain.com/filePath1")).thenReturn(200);
    when(this.parser.getResponseStatusCode("http://specific-domain.com/filePath2")).thenReturn(200);

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

    Assert.assertEquals(new HashSet<>(Arrays.asList(bug2, bug1)), this.analyzer.analyze("http://specific-domain.com"));
  }

}
