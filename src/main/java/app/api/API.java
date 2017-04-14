package app.api;

import app.analyze.Bug;

import java.util.List;

public interface API {

  List<Bug> getAllBugs();

  List<Bug> getBugs(String url);
}
