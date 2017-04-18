package app.analyze;

import java.util.Set;

public interface Inspector {

  Set<Bug> inspect(String url);
}
