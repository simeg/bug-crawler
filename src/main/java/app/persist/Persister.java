package app.persist;

import app.analyze.Bug;

import java.util.Collection;

public interface Persister<T> {

  boolean storeBug(Bug bug);

  boolean storeAllBugs(Collection<Bug> urls);

  boolean store(T url);

  boolean storeAll(Collection<T> urls);
}
