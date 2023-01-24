package org.sid.demospringcloudstreamskafka.services;

import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PageEventService {
    @Bean
    public Consumer<PageEvent> pageEventConsumer() {
        System.out.println("called");
        return (input) -> {
            // elle recoit des msgs de kafka et les affiche
            // si vs voullez store utilisez JPA
            System.out.println("****************");
            System.out.println(input.toString());
            System.out.println("****************");
        };
    }

    @Bean
    public Supplier<PageEvent> pageEventSupplier() {
        // pas d input par contre elle va produire des outputs
        return () -> new PageEvent(
                Math.random() > 0.5 ? "P1" : "P2",
                Math.random() > 0.5 ? "U1" : "U2",
                new Date(),
                new Random().nextInt(9000));
    }
    @Bean
    public Function<PageEvent,PageEvent> pageEventFunction(){
        return (input)->{
            input.setName("Page:"+input.getName().length());
            input.setUser("User=>1");
            return input;
        };
    }
}
