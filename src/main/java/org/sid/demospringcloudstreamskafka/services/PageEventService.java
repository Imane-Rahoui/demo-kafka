package org.sid.demospringcloudstreamskafka.services;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    @Bean
    public Function<KStream<String,PageEvent>, KStream<String,Long>> kStreamFunction(){
        return (input)->{
            // on va lire du topic R2 pcq le supplier ecrit dans ce topic
            // chaque enregistrement s'appelle un data record
            // il y'a 2 partie : key et value
            // pour le moment key = null pcq on envoie des json comme value
            // on va produire en sortie des statistiques
            // exp la PAGE a ete visité combien de fois
            // un ktable : est le resultat
            return input
                    .filter((k,v)->v.getDuration()>100)// filtrer data
                    .map((k,v)->new KeyValue<>(v.getName(),0L))// la cle mtn c est le nom de la page et la val c'est 0
                    .groupBy((k,v)->k, Grouped.with(Serdes.String(),Serdes.Long()))// la clé va etre deserialisé et serialisé en format String et la valeur long - groupBy return un stream
                    .windowedBy(TimeWindows.of(Duration.ofSeconds(5))) // statistique sur les 5 derniers cas - windowedBy : produit un ktable c pour ca qu on va ajouter to stream et map pour organiser
                    .count(Materialized.as("page-count"))
                    //vue materializé : store qui s appel page-cont : on peut recuperer ces donnees et les afficher dans une application web
                    // page-cont est un store ou ktable ou table
                    // grouper par rapport a la clé
                    .toStream() // le res est un stream
                    .map((k,v)->new KeyValue<>("=>"+k.window().startTime()+k.window().endTime()+":"+k.key(),v)); // pour avoir cet affichage =>2023-01-24T19:32:45Z2023-01-24T19:32:50Z:P1
                        // on voit l'évolution et non pas le cumul
        };
    }
}
