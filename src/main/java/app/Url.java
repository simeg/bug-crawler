package app;

/**
 * TODO
 * The type Url will hold all information regarding the URL.
 * This would be metadata like root domain, if it's valid url etc.
 * This functionality is today spread out across functions in different classes
 * which makes it a bit cumbersome to work with. It would be nice to have it
 * accessible in one single place. This requires quite some refactoring though
 * as all methods and functions that today accept a String of a URL will have
 * this class as the type instead.
 *
 * Let's take small steps. This is step one, creating this.
 * TODOs
 * - Use this type instead of String for url everywhere, just make it work. No more.
 * - On creation of this class, create field for root domain etc. and fill them
 *    - Root domain (domain.com)
 *    - Directories e.g. (domain.com/directory/directory/file.php)
 *    - File name (domain.com/file.php)
 *    - File extension (domain.com/file.php)
 * - On creating of this class, add more metadata such as isValid
 *
 * BEFORE STARTING!
 * Look at what other people have done first, this one sounds promising:
 * https://medium.com/square-corner-blog/okhttps-new-url-class-515460eea661
 * https://github.com/square/okhttp
 */
public final class Url {

  public final String url;

  public Url(String url) {
    this.url = url;
  }

}
