FROM openjdk:8-jre-alpine3.9
 
# copy the packaged jar file into our docker image
COPY ./defi-tweet-stream/target/defi-tweet-stream-1.0-SNAPSHOT-jar-with-dependencies.jar /target/defi-tweet-stream-1.0-SNAPSHOT-jar-with-dependencies.jar


# set the startup command to execute the jar
CMD ["java", "-jar", "/target/defi-tweet-stream-1.0-SNAPSHOT-jar-with-dependencies.jar"]