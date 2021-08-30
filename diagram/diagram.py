from diagrams import Diagram, Cluster, Edge
from diagrams.custom import Custom
from diagrams.onprem.queue import Kafka
from diagrams.elastic.elasticsearch import Elasticsearch
# with Diagram("Custom with local icons\n Can be downloaded here: \nhttps://creativecommons.org/about/downloads/", show=False, filename="custom_local", direction="LR"):
#   cc_heart = Custom("Creative Commons", "./my_resources/cc_heart.black.png")
#   cc_attribution = Custom("Credit must be given to the creator", "./my_resources/cc_attribution.png")

#   cc_sa = Custom("Adaptations must be shared\n under the same terms", "./my_resources/cc_sa.png")
#   cc_nd = Custom("No derivatives or adaptations\n of the work are permitted", "./my_resources/cc_nd.png")
#   cc_zero = Custom("Public Domain Dedication", "./my_resources/cc_zero.png")

#   with Cluster("Non Commercial"):
#     non_commercial = [Custom("Y", "./my_resources/cc_nc-jp.png") - Custom("E", "./my_resources/cc_nc-eu.png") - Custom("S", "./my_resources/cc_nc.png")]

#   cc_heart >> cc_attribution
#   cc_heart >> non_commercial
#   cc_heart >> cc_sa
#   cc_heart >> cc_nd
#   cc_heart >> cc_zero


# with Diagram("Tweet Ingester backed by Kafka for Defi Projects on Polkadot's chain", show=False, filename="diagram", direction="LR"):
    
#     twitter = Custom("Twitter API", "./pics/twitter.png")
    
#     with Cluster("k8s cluster"):
#         twitter >> Kafka("Kafka Twitter Source")

#         kafka_defi_tweet_topic = Kafka("Topic: defi-tweet")

#         Kafka("Kafka Twitter Source") >> kafka_defi_tweet_topic
#         kafka_defi_tweet_topic >> Elasticsearch("defi-tweet stream on dejavu UI")
#         kafka_defi_tweet_topic >> Custom("Postgres", "./pics/postgres.png")

#         kafka_above_10000_followers_tweets = Kafka("Topic: above-10000-followers-tweets")

#         kafka_defi_tweet_topic >> kafka_above_10000_followers_tweets
#         kafka_above_10000_followers_tweets >> Kafka("Topic: fact-tweet")
#         kafka_above_10000_followers_tweets >> Kafka("Topic: user-profile")

with Diagram("Tweet Ingester backed by Kafka for Defi Projects on Polkadot's chain by Roger Yau", show=False, filename="diagram", direction="LR"):
    
    twitter = Custom("Twitter API", "./pics/twitter.png")
    

    with Cluster("k8s cluster"):

        with Cluster("microservice for kafka,zookeeper & landoop microservice"):
            kafka_twitter_source = Kafka("Kafka Twitter Source")
            kafka_defi_tweet_topic = Kafka("Topic: defi-tweet")
            kafka_above_10000_followers_tweets = Kafka("Topic: above-10000-followers-tweets")
            kafka_fact_tweet = Kafka("Topic: fact-tweet")
            kafka_user_profile = Kafka("Topic: user-profile")

        with Cluster("microservice for elasticsearch"):
            defi_tweet_stream_on_dejavu = Elasticsearch("defi-tweet stream shown on dejavu UI")

        with Cluster("microservice for postgres"):
            postgres = Custom("Postgres", "./pics/postgres.png")

        twitter >> kafka_twitter_source
        kafka_twitter_source >> kafka_defi_tweet_topic
        kafka_defi_tweet_topic >> Edge(label="kafka sink connector") >> defi_tweet_stream_on_dejavu
        kafka_defi_tweet_topic >> Edge(label="kafka sink connector") >> postgres

        kafka_defi_tweet_topic >> Edge(label="Kstream filter") >> kafka_above_10000_followers_tweets
        kafka_above_10000_followers_tweets >> Edge(label="Kstream split") >> kafka_fact_tweet
        kafka_above_10000_followers_tweets >> Edge(label="Kstream split") >> kafka_user_profile