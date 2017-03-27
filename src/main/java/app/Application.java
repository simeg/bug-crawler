package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages={"app"})
public class Application {

  public static void main(String[] args) {
    Application app = new Application();
    app.start();
    SpringApplication.run(app.getClass(), args);
  }

  private void start() {
    ScheduledExecutorService scheduledExecutorService =
        Executors.newScheduledThreadPool(1);
    ScheduledFuture scheduledFuture =
        scheduledExecutorService.scheduleWithFixedDelay(
            // Can be Runnable or lambda. Can lambda replace any class?
            () -> {
              String content = readFromUrl("http://vecka.nu/");
              System.out.println("#####################################");
              System.out.println(parseWeekNumber(content));
              System.out.println("#####################################");
            },
            0,
            5,
            TimeUnit.SECONDS
        );
  }

  private String readFromUrl(String theURL) {
    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;
    StringBuffer sb = new StringBuffer();

    try {
      u = new URL(theURL);
      is = u.openStream();
      dis = new DataInputStream(new BufferedInputStream(is));
      String line;
      while ((s = dis.readLine()) != null) {
        line = s + "\n";
        sb.append(line);
      }
    } catch (MalformedURLException mue) {
      System.out.println("Ouch - a MalformedURLException happened.");
      mue.printStackTrace();
      System.exit(1);
    } catch (IOException ioe) {
      System.out.println("Oops- an IOException happened.");
      ioe.printStackTrace();
      System.exit(1);
    } finally {
      try {
        is.close();
      } catch (IOException ignored) {}
    }
    return sb.toString();
  }

  private String parseWeekNumber(String html) {
    String output;

    Pattern replaceWhitespacePattern = Pattern.compile("\\s");
    Matcher matcher;
    matcher = replaceWhitespacePattern.matcher(html);
    output = matcher.replaceAll(" ");

    Pattern removeHTMLTagsPattern = Pattern.compile("]*>");
    matcher = removeHTMLTagsPattern.matcher(output);
    output = matcher.replaceAll("");

    Pattern leaveOnlyAlphaNumericCharactersPattern = Pattern.compile("[^0-9a-zA-Z ]");
    matcher = leaveOnlyAlphaNumericCharactersPattern.matcher(output);
    output = matcher.replaceAll("");

    List<String> htmlElements = Arrays.asList(output.split(" "));

    // Remove empty elements
    List<String> nonEmptyHtmlElements = htmlElements.stream()
        .filter(e -> !e.equals(""))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    int index = nonEmptyHtmlElements.indexOf("week");
    String week = nonEmptyHtmlElements.get(index + 1);

    return week;
  }

}
