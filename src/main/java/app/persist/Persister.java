package app.persist;

import app.analyze.Bug;

import java.util.Collection;

public interface Persister<T> {

  boolean storeBug(Bug bug);

  boolean storeBugs(Collection<Bug> urls);

  boolean store(T url);

  boolean store(Collection<T> urls);
}
