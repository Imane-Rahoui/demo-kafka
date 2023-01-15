package org.sid.demospringcloudstreamskafka.services;

import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PageEventService {
  /*  @Bean
    public Consumer<PageEvent> pageEventConsumer(){
        System.out.println("called");
        return (input)->{
            // elle recoit des msgs de kafka et les affiche
            // si vs voullez store utilisez JPA
            System.out.println("****************");
            System.out.println(input.toString());
            System.out.println("****************");
        };
    }*/
  @Bean
  public Consumer<PageEvent> pageEventConsumer(){
      return (input)->{
          System.out.println("**********************");
          System.out.println(input.toString());
          System.out.println("**********************");
      };
  }
}
