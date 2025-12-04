package lk.jiat.envy.config;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {

    public AppConfig() {
        packages("lk.jiat.envy.controller");
        packages("lk.jiat.envy.middleware");
    }
}
