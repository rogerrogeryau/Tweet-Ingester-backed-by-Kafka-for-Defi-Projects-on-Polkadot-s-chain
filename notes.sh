#!/bin/bash

# Start kafka cluster
docker-compose up kafka-cluster elasticsearch postgres
# Wait 2 minutes for the kafka cluster to be started

###############
# 1) ElasticSearch Sink
http://127.0.0.1:9200/
# Go to the connect UI and apply the configuration at :
sink/elasticsearch/sink-elastic-defi-tweet-distributed.properties
# Visualize the data at:
http://127.0.0.1:9200/_plugin/dejavu
# http://docs.confluent.io/3.1.1/connect/connect-elasticsearch/docs/configuration_options.html
# Counting the number of tweets:
http://127.0.0.1:9200/defi-tweet/_count


# 2) Postgres
#login postgres
psql -U postgres

#query the fields in targeted table sourced from kafka topic `defi-tweet`
select created_at, text from public."defi-tweet" order by created_at desc;