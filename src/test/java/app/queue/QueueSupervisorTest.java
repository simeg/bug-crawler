package app.queue;

import app.analyze.Bug;
import app.persist.PsqlPersister;
import app.request.UrlRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueueSupervisorTest {

  private QueueSupervisor supervisor;

  @Before
  public void setUp() throws Exception {
    final PsqlPersister<Bug> bugPersister = Mockito.mock(PsqlPersister.class);
    final PsqlPersister<String> persister = Mockito.mock(PsqlPersister.class);
    final PsqlPersister<UrlRequest> requestQueue = Mockito.mock(PsqlPersister.class);
    supervisor = QueueSupervisor.create(bugPersister, persister, requestQueue);
  }

  @Test
  public void testIsUnique() throws Exception {
    final Set<String> cache = new HashSet<>();
    Assert.assertTrue(supervisor.isUnique(cache, "specific-url-1"));
    Assert.assertEquals(cache.size(), 1);
    Assert.assertTrue(supervisor.isUnique(cache, "arbitrary-url-1"));
    Assert.assertEquals(cache.size(), 2);
    Assert.assertTrue(supervisor.isUnique(cache, "arbitrary-url-2"));
    Assert.assertEquals(cache.size(), 3);
    Assert.assertFalse(supervisor.isUnique(cache, "specific-url-1"));
    Assert.assertEquals(cache.size(), 3);

    final Set<String> expectedCache = new HashSet<>();
    expectedCache.addAll(
        Arrays.asList(
            "specific-url-1",
            "arbitrary-url-1",
            "arbitrary-url-2"
        ));
    Assert.assertEquals(expectedCache, cache);
  }

  @Test
  public void testAddToAnalyzeCollectionCache() throws Exception {
    final Set<String> cache = new HashSet<>();
    final List<String> hasDuplicatedElements =
        Arrays.asList(
            "DUPLICATED-1",
            "DUPLICATED-1",
            "DUPLICATED-2",
            "DUPLICATED-2",
            "irrelevant-1",
            "irrelevant-2",
            "irrelevant-3");

    final Set<String> uniqueElements = supervisor.getUniqueElements(cache, hasDuplicatedElements);
    final Set<String> expected = new HashSet<>();
    expected.addAll(
        Arrays.asList(
            "DUPLICATED-1",
            "DUPLICATED-2",
            "irrelevant-2",
            "irrelevant-1",
            "irrelevant-3"));

    Assert.assertEquals(cache.size(), 5);
    Assert.assertEquals(expected, cache);
    Assert.assertEquals(expected, uniqueElements);
  }
}
