package app.persist;

import app.analyze.Bug;

import java.util.Collection;
import java.util.List;

public interface Persister<T> {

  boolean storeBug(Bug bug);

  boolean storeAllBugs(Collection<Bug> urls);

  boolean store(T url);

  boolean storeAll(Collection<T> urls);

  List<Bug> getAllBugs();

  Bug getBugs(String url);
}
