package app.analyze;

import app.plugin.Plugin;
import app.url.Url;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class Analyzer {

  private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

  private final List<Plugin> plugins;

  public Analyzer(List<Plugin> plugins) {
    this.plugins = plugins;
  }

  public ImmutableSet<Bug> analyze(Url url) {
    LOG.info("Will now analyze URL: {}", url.rawUrl);
    final Set<Bug> result = Sets.newHashSet();

    plugins.forEach((plugin ->
        result.addAll(plugin.inspect(url.rawUrl))
    ));

    return ImmutableSet.copyOf(result);
  }

}
