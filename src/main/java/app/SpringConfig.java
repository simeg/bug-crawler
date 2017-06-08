package app;

import app.db.PsqlContextHandler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

  @Bean
  public DSLContext getPsqlContext() {
    final Config conf = ConfigFactory.load();
    return PsqlContextHandler.getContext(
        "org.postgresql.Driver",
        conf.getString("db.host"),
        conf.getInt("db.port"),
        conf.getString("db.name"),
        conf.getString("db.username"),
        conf.getString("db.password")
    );
  }
}
