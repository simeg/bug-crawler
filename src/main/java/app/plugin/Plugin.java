package app.plugin;

import app.analyze.Bug;
import com.google.common.collect.ImmutableSet;

public interface Plugin {

  ImmutableSet<Bug> inspect(String url);

  String getDescription();

}
