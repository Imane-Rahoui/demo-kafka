package org.sid.demospringcloudstreamskafka.web;

import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
public class PageEventRestController {

    @Autowired
    private StreamBridge streamBridge; // ça fonctionne vc ts les brokers

    @GetMapping("/publish/{topic}/{name}")
    public PageEvent publish(@PathVariable String topic, @PathVariable String name) {
        PageEvent pageEvent = new PageEvent(name, Math.random() > 0.5 ? "U1" : "U2", new Date(), new Random().nextInt(9000)); // entier entre 0 et 9000
        streamBridge.send(topic, pageEvent);
        return pageEvent;
    }

    // pr le consumer il y'a deux facons :
    // @messagelistener de spring cloud stream ms une version deprecié pcq il y a mieux
    // utiliser la programmation fonctionnelle (qu on va utilisé)
}
