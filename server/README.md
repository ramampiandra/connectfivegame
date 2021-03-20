How to compile the program

go to the project , cd connectfivegame\server>

enter > mvn -DskipTests clean package

go to target now, cd /target (server\target\server-0.0.1-SNAPSHOT.jar)

And type : java -jar server-0.0.1-SNAPSHOT.jar

You can also change the name of the jar file name 

Note : 

The server is running when the following message is displayed on the console : 

2021-03-20 15:30:01.317  INFO 11164 --- [           main] com.server.MainApplication               : Started MainApplication in 4.692 seconds (JVM running for 5.415)