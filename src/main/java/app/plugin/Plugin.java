package app.plugin;

import app.analyze.Bug;

import java.util.Set;

public interface Plugin {

  Set<Bug> inspect(String url);
}
