package org.rrabarg.teamcaptain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

@Configuration
@EnableReactor
public class ReactorConfig {

    @Bean
    public Reactor rootReactor(Environment env) {
        return Reactors.reactor().env(env).synchronousDispatcher().get();
    }

    @Bean
    Environment env() {
        return new Environment();
    }

}