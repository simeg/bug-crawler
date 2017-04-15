package app.analyze;

import app.parse.Parser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.assertj.core.util.Sets;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AnalyzerTest {

  private Analyzer analyzer;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    final Config conf = ConfigFactory.load();
    List<Object> paths = conf.getList("analyzer.filePaths").unwrapped();

    this.parser = Mockito.mock(Parser.class);
    this.analyzer = new Analyzer(parser, paths);
  }

  @Test
  public void testAnalyze404Response() throws Exception {
    // 404 occurs when website is loaded but no content on website
    when(this.parser.getResponse(any(String.class))).thenReturn(new Mock404Response());

    Assert.assertEquals(Sets.newHashSet(), this.analyzer.analyze("http://specific-domain.com"));
  }

  @Test
  public void testAnalyzeThrowException() throws Exception {
    // Exception occurs when website is unable to load
    when(this.parser.getResponse(any(String.class))).thenThrow(new IOException());

    Assert.assertEquals(Sets.newHashSet(), this.analyzer.analyze("http://specific-domain.com"));
  }

  @Test
  public void testAnalyze() throws Exception {
    // TODO: Test happy path(s)
  }

  class Mock404Response implements Connection.Response {
    @Override
    public int statusCode() {
      return 404;
    }

    @Override
    public String statusMessage() {
      return null;
    }

    @Override
    public String charset() {
      return null;
    }

    @Override
    public Connection.Response charset(String charset) {
      return null;
    }

    @Override
    public String contentType() {
      return null;
    }

    @Override
    public Document parse() throws IOException {
      return null;
    }

    @Override
    public String body() {
      return null;
    }

    @Override
    public byte[] bodyAsBytes() {
      return new byte[0];
    }

    @Override
    public URL url() {
      return null;
    }

    @Override
    public Connection.Response url(URL url) {
      return null;
    }

    @Override
    public Connection.Method method() {
      return null;
    }

    @Override
    public Connection.Response method(Connection.Method method) {
      return null;
    }

    @Override
    public String header(String name) {
      return null;
    }

    @Override
    public Connection.Response header(String name, String value) {
      return null;
    }

    @Override
    public boolean hasHeader(String name) {
      return false;
    }

    @Override
    public boolean hasHeaderWithValue(String name, String value) {
      return false;
    }

    @Override
    public Connection.Response removeHeader(String name) {
      return null;
    }

    @Override
    public Map<String, String> headers() {
      return null;
    }

    @Override
    public String cookie(String name) {
      return null;
    }

    @Override
    public Connection.Response cookie(String name, String value) {
      return null;
    }

    @Override
    public boolean hasCookie(String name) {
      return false;
    }

    @Override
    public Connection.Response removeCookie(String name) {
      return null;
    }

    @Override
    public Map<String, String> cookies() {
      return null;
    }
  }
}
