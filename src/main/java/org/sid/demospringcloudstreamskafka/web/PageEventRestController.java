package org.sid.demospringcloudstreamskafka.web;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class PageEventRestController {

    @Autowired
    private StreamBridge streamBridge; // ça fonctionne vc ts les brokers
    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @GetMapping("/publish/{topic}/{name}")
    public PageEvent publish(@PathVariable String topic, @PathVariable String name) {
        PageEvent pageEvent = new PageEvent(name, Math.random() > 0.5 ? "U1" : "U2", new Date(), new Random().nextInt(9000)); // entier entre 0 et 9000
        streamBridge.send(topic, pageEvent);
        return pageEvent;
    }

    // pr le consumer il y'a deux facons :
    // @messagelistener de spring cloud stream ms une version deprecié pcq il y a mieux
    // utiliser la programmation fonctionnelle (qu on va utilisé)

    //une fois le client conn - le serveur va passer les données au client
    // flux : un object qui va etre converti en format json
    // clé : nom page et value le nbr

    // visualiser les resultats  : http://localhost:8080/analytics
    @GetMapping(path = "/analyticsfi/{page}",produces = MediaType.TEXT_EVENT_STREAM_VALUE) // produces pr dire je vais retouner ce type
    public Flux<Map<String, Long>>  analyticsFiltered(@PathVariable String page){
        return Flux.interval(Duration.ofSeconds(1)) // genere moi un stream et un enregistrement dans ce stream chaque seconde
                .map(sequence->{
                    // pratiqu ms vous pouvez utilisez ksql pr ecrire avec du sql
                    Map<String,Long> stringLongMap=new HashMap<>();
                    // interoger le store page-count | on doit specifier le type de store dans ce cas on a utilisé de type windows sinon keyValueStore()
                    ReadOnlyWindowStore<String, Long> windowStore = interactiveQueryService.getQueryableStore("page-count", QueryableStoreTypes.windowStore());
                    Instant now=Instant.now();
                    Instant from=now.minusMillis(5000);
                    // fetchAll : comme un select le calcule des 5 derniers secondes | mn db - 5s l db
                    //KeyValueIterator<Windowed<String>, Long> fetchAll = windowStore.fetchAll(from, now);
                    WindowStoreIterator<Long> fetchAll = windowStore.fetch(page, from, now); // si vous voulez par des criteres exp le res de la page P1
                    while (fetchAll.hasNext()){
                        KeyValue<Long, Long> next = fetchAll.next();
                        /*KeyValue<Windowed<String>, Long> next = fetchAll.next();
                        // l'enregistrement qu'on va return
                        //stringLongMap.put(next.key.window().startTime(),next.value); // par exp d'autres params
                        stringLongMap.put(next.key.key(),next.value);*/
                        stringLongMap.put(page, next.value);
                    }
                    return stringLongMap;
                }).share(); // le stream est partagé par plusieurs users sinon chaque user recoit son propre flux
    }

    @GetMapping(path = "/analytics",produces = MediaType.TEXT_EVENT_STREAM_VALUE) // produces pr dire je vais retouner ce type
    public Flux<Map<String, Long>>  analytics(){
        return Flux.interval(Duration.ofSeconds(1)) // genere moi un stream et un enregistrement dans ce stream chaque seconde
                .map(sequence->{
                    Map<String,Long> stringLongMap=new HashMap<>();
                    // interoger le store page-count | on doit specifier le type de store dans ce cas on a utilisé de type windows sinon keyValueStore()
                    ReadOnlyWindowStore<String, Long> windowStore = interactiveQueryService.getQueryableStore("page-count", QueryableStoreTypes.windowStore());
                    Instant now=Instant.now();
                    Instant from=now.minusMillis(5000);
                    // fetchAll : comme un select le calcule des 5 derniers secondes | mn db - 5s l db
                    KeyValueIterator<Windowed<String>, Long> fetchAll = windowStore.fetchAll(from, now);
                    //WindowStoreIterator<Long> fetchAll = windowStore.fetch("P1", from, now); // si vous voulez par des criteres exp le res de la page P1
                    while (fetchAll.hasNext()){
                        KeyValue<Windowed<String>, Long> next = fetchAll.next();
                        // l'enregistrement qu'on va return
                        //stringLongMap.put(next.key.window().startTime(),next.value); // par exp d'autres params
                        stringLongMap.put(next.key.key(),next.value);
                    }
                    return stringLongMap;
                }).share(); // le stream est partagé par plusieurs users sinon chaque user recoit son propre flux
    }
}
