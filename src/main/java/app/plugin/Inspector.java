package app.plugin;

import java.util.Set;

public interface Inspector<T> {

  Set<T> inspect(String url);
}
