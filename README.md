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
(dependencies -> lombok / spring web / spring for apache kafka / spring for apache kafka streams / cloud stream )
on a utilisé cloud stream vc kafka sinon on peut enlever les dep de kafka et les remplacer avec un autre (rabbitMQ / activeMQ ..)

### creation de la classe PageEvent - PageEventRestController puis lancement du serveur et tester sur nav web http://localhost:8080/publish/R1/imana
### sans oublier de garder kafka-console-consumer lancé pour checker si ça fonctionne parfaitement et sur le bon topic. le msg est sérializé par defaut format json
