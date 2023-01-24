# Explication :

![explication](https://user-images.githubusercontent.com/77898496/212558167-0d032f4b-0cb2-4f6e-a572-deb331113f91.png)

# Preparation de l'envir : 
  download kafka from https://kafka.apache.org/downloads || Scala 2.13  - kafka_2.13-3.3.1.tgz (asc, sha512)

# First Step : (lancer zookeeper)
D:\5IIR\J2EE\TPS\TP5\kafka_2.13-3.3.1\bin>``start windows\zookeeper-server-start.bat ../config/zookeeper.properties``

# Second one : (lancer kafka server)
D:\5IIR\J2EE\TPS\TP5\kafka_2.13-3.3.1\bin>``start windows\kafka-server-start.bat ../config/server.properties``

(par defaut kafka suppose que zookeeper est lancé en localhost port:2181 et kafka 9092)
(log de kafka au nv du dossier D:\tmp)
(ça allourdie alors comme sollution il faut supprimer le contenu de ce dossier et redemarrer le serveur)

# demarrage de kafka-console-producer et kafka-console-consumer pour faire des tests
D:\5IIR\J2EE\TPS\TP5\kafka_2.13-3.3.1\bin>``start windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic R1``

(il attends - btw il a signalé une erreur au debut alors on ferme la fenetre puis en relance a nv)

D:\5IIR\J2EE\TPS\TP5\kafka_2.13-3.3.1\bin>``start windows\kafka-console-producer.bat --broker-list localhost:9092 --topic R1``
(le meme topic)

# Creation app :

## Case1 :

![cas1](https://user-images.githubusercontent.com/77898496/212558177-9a401640-424b-4190-8ce6-d448ad9007e6.png)

(dependencies -> lombok / spring web / spring for apache kafka / spring for apache kafka streams / cloud stream )
on a utilisé cloud stream vc kafka sinon on peut enlever les dep de kafka et les remplacer avec un autre (rabbitMQ / activeMQ ..)

### creation de la classe PageEvent - PageEventRestController puis lancement du serveur et tester sur nav web http://localhost:8080/publish/R1/imana
### sans oublier de garder kafka-console-consumer lancé pour checker si ça fonctionne parfaitement et sur le bon topic. le msg est sérializé par defaut format json

### Resultat :

![cas1Exec](https://user-images.githubusercontent.com/77898496/212558196-9fe77ac9-764b-40b6-b36a-5f9511ee5cf8.png)

## Cas 2 :

![cas2](https://user-images.githubusercontent.com/77898496/212558353-6ea9fdf7-ced9-4cc2-b534-48f3d80a2343.png)

pr le consumer il y'a deux facons :
    - @messagelistener de spring cloud stream ms une version deprecié pcq il y a mieux
    - utiliser la programmation fonctionnelle (qu on va utilisé)

### Creation du service : PageEventService

cet evenement va permettre d'afficher les messages de kafka
quand on retourne type consumer -> spring cloud va prendre le reste en charge | c'est une abstraction de kafka le meme code va fonctionne sur rabbitMQ
il faut ajouter un channel qui porte le meme nom que la fct alors : <br>
	- il faut ajouter des params au nv du file application.properties : <br>
	``spring.cloud.function.definition=pageEventConsumer``<br>
	``spring.cloud.stream.bindings.pageEventConsumer-in-0.destination=R1`` <br>
	- pour tester : 
	<p align="center">
![Capture d’écran 2023-01-24 174530](https://user-images.githubusercontent.com/77898496/214355329-ef931d5b-1fb2-4a95-8c0e-8e6be587828c.png)  ![2](https://user-images.githubusercontent.com/77898496/214357794-ab09f594-7212-4b9b-81dd-368478a57bd6.png)
	</p>

## Case 3 :
Creation d'un supplier, qui va s'occuper d'envoyer un message à chaque seconde.
Pour le faire : <br>
- Il faut creer ``@Bean`` une fonction Supplier ( comme au nv du consumer en specifiant le type de retour Supplier du package java.util.function Spring cloud stream va comprendre qu'il s'agit d'un supplier. [par défaut chaque seconde va produire un event et cette fct va s'executer] <br>
- La fonction Supplier va produire des messages dans topic donc le nom par defaut porte le meme nom que la fct supplier : ``spring.cloud.stream.bindings.pageEventSupplier-out-0.destination=R2``
- Spring suppose que par defaut vous utilisez soit : <br>
	- supplier
	- consumer 
	- function (cas 4) <br>
  pour lui signaler que c'est pas le cas il faut ajouter la ligne suivante pour qu'il puisse les déployer dan son contexte : ``spring.cloud.function.definition=pageEventConsumer;pageEventSupplier`` (Cette notion facilite le deploiment dans un contexte serverless exp lambda function d'Amazon) <br>
### Resultat :
- D'abord il faut lancer un consumer dans le topic R2 : <br> 
D:\5IIR\J2EE\TPS\TP5\kafka_2.13-3.3.1\bin> ``start windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic R2``
![Capture d’écran 2023-01-24 183339](https://user-images.githubusercontent.com/77898496/214366014-302090c0-fc2d-4feb-8109-f388dbcf48ac.png)
- Pour change le timing il suffit d'ajouter la ligne suivante ``spring.cloud.stream.poller.fixed-delay=100`` | normalement 1000 c'est 1s par def | 100 = ms (mtn + rapide)
![Capture d’écran 2023-01-24 183857](https://user-images.githubusercontent.com/77898496/214367427-3dce784a-64fe-4785-bb16-682af8740da3.png)

