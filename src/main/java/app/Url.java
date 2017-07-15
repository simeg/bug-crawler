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
 * - On creating of this class, add more metadata such as isValid
 */
public final class Url {

  public final String url;

  public Url(String url) {
    this.url = url;
  }

}
