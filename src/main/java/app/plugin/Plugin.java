package app.plugin;

import java.util.Set;

public interface Plugin<T> {

  Set<T> inspect(String url);
}
