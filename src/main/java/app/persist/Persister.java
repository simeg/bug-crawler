package app.persist;

import java.util.Collection;

public interface Persister<T> {

  boolean store(T url);

  boolean storeAll(Collection<T> urls);

}
