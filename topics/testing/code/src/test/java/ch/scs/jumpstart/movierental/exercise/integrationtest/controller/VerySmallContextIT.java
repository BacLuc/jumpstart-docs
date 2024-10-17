package ch.scs.jumpstart.movierental.exercise.integrationtest.controller;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(properties = {"logging.level.org.springframework.test.context.cache=DEBUG"})
@ContextConfiguration(classes = {ContextToCache.class})
class AddElementToCacheIT {
    @Test
    void not_much() {

    }
}

@Configuration
class ContextToCache {

}

@SpringBootTest(properties = {"logging.level.org.springframework.test.context.cache=DEBUG"})
@ContextConfiguration(classes = {NoBeans.class})
@DirtiesContext
class VerySmallContextIT {
    @Test
    void not_much() {

    }
}

@Configuration
class NoBeans {

}

@SpringBootTest(properties = {"logging.level.org.springframework.test.context.cache=DEBUG"})
@ContextConfiguration(classes = {OneBean.class})
@DirtiesContext
class OtherSmallContextIT {

    @Test
    void not_much() {

    }
}

@Configuration
class OneBean {
    @Bean
    Object bean() {
        return new Object();
    }
}


@SpringBootTest(properties = {"logging.level.org.springframework.test.context.cache=DEBUG"})
@ContextConfiguration(classes = {ContextToCache.class})
class CheckCacheSizeLaterIT {
    @Test
    void not_much() {

    }
}
