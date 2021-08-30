#!/bin/bash

# jump into kafka broker container 
docker exec -it <container_id> bash

# create topics
kafka-topics --create --topic defi-tweet --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
kafka-topics --create --topic above-10000-followers-tweets --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
kafka-topics --create --topic fact-tweet --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181
kafka-topics --create --topic user-profile --partitions 3 --replication-factor 1 --zookeeper 127.0.0.1:2181

# Start a console consumer on that topic (if needed)
kafka-console-consumer --topic defi-tweet --bootstrap-server 127.0.0.1:9092

# list topic
kafka-topics --zookeeper 127.0.0.1:2181 --list

# run the jar that is built locally
java -jar /app/defi-tweet-stream-1.0-SNAPSHOT-jar-with-dependencies.jar