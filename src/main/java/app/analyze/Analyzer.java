package app.analyze;

import app.plugin.Plugin;
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

  public ImmutableSet<Bug> analyze(String url) {
    LOG.info("Will now analyze URL: {}", url);
    final Set<Bug> result = Sets.newHashSet();

    plugins.forEach((plugin ->
        result.addAll(plugin.inspect(url))
    ));

    return ImmutableSet.copyOf(result);
  }

}
