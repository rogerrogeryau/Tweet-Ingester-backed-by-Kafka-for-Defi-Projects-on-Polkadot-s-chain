package com.rogery.defi.tweet.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import java.util.Properties;

public class TweetFilter {

    private static final ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "tweet-filter-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> defiTweet = builder.stream("defi-tweet");

        // for debugging - 1
//        DefiTweet.foreach((k, v)->{
//            //System.out.println();
//            extractUserFollowersInTweet(v);
//        });

        // for debugging - 2 (depreciated)
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        DefiTweet.foreach((k, v)->{
//            try {
//                Map<String, Object> value = mapper.readValue( v, Map.class );
//                System.out.println(
//                        ((Map<String, Object>)value.get("payload")).get("id")
//                        + "," + ((Map<String, Object>)value.get("payload")).get("created_at")
//                        + "," +((Map<String, Object>)value.get("payload")).get("text").toString().replaceAll("[\r\n]+", " ")
//                );
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        KStream<String, String> filteredStream = defiTweet.filter(
                // filter for tweets which has a user of over 10000 followers
                (k, jsonTweet) ->  extractUserFollowersInTweet(jsonTweet) > 10000
        );

        filteredStream.to("above-10000-followers-tweets");

        KStream<String, String> tweetDataStream = filteredStream.map((k, v) -> new KeyValue<>(k, extractTweetDataAsString(v)));
        tweetDataStream
            .selectKey((k, v) -> v.toString().split(",")[0])
//                //debugging
            .peek((k,v)->{
                System.out.println(k);
            })
            .to("fact-tweet");


        KStream<String, String> userStream = filteredStream.map((k, v) -> new KeyValue<>(k, extractUserDataAsString(v)));
        userStream
                .selectKey((k, v) -> v.toString().split(",")[0])
//                //debugging
                .peek((k,v)->{
                    System.out.println("key: " + k + "value: " + v);
                })
                .to("user-profile");


        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp(); // only do this in dev - not in prod
        streams.start();

        // print the topology
//        streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
    private static JsonParser jsonParser = new JsonParser();


    private static Integer extractUserFollowersInTweet(String tweetJson){
        // gson library
        try {

            //for debugging
//            System.out.println(jsonParser.parse(tweetJson)
//                    .getAsJsonObject()
//                    .get("payload")
//                    .getAsJsonObject()
//                    .get("user")
//                    .getAsJsonObject()
//                    .get("followers_count")
//
//            );

            return jsonParser.parse(tweetJson)
                    .getAsJsonObject()
                    .get("payload")
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .get("followers_count")
                    .getAsInt();
        }
        catch (NullPointerException e){
            return 0;
        }
    }

    private static String extractTweetDataAsString(String tweetJson){
        //tweet_id
        return
            jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("id")
            .getAsString()

            //created_at
            + "," + jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("created_at")
            .getAsString()


            //user_id
            + "," + jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("user")
            .getAsJsonObject()
            .get("id")
            .getAsString()

            // //text
            + "," + jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("text")
            .getAsString()
            .toString()
            .replaceAll("[\r\n]+", " ")

            // // lang
            + "," + jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("lang")
            .getAsString()
            .toString()

            // // is_retweet
            + "," + jsonParser.parse(tweetJson)
            .getAsJsonObject()
            .get("payload")
            .getAsJsonObject()
            .get("is_retweet")
            .getAsString()
            .toString();


    }

    private static String extractUserDataAsString(String tweetJson){
        return jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("id")
                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("name")
                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("name")
                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("screen_name")
                .getAsString()

//            + "\t" + jsonParser.parse(tweetJson)
//                .getAsJsonObject()
//                .get("payload")
//                .getAsJsonObject()
//                .get("user")
//                .getAsJsonObject()
//                .get("location")
//                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("friends_count")
                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("followers_count")
                .getAsString()

            + "," + jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("user")
                .getAsJsonObject()
                .get("statuses_count")
                .getAsString();

    }

//    class tweetSchema {
//        private String tweet_id;
//        private String created_at;
//        private String user_id;
//        private String text;
//        private String lang_code;
//        private String is_retweet;
//    }


}
