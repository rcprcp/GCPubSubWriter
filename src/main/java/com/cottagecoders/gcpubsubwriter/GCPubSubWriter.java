package com.cottagecoders.gcpubsubwriter;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GCPubSubWriter {
  // slf4j
  private static final Logger LOG2 = LoggerFactory.getLogger(GCPubSubWriter.class);


  private static final java.util.logging.Logger log =
      java.util.logging.Logger.getLogger(GCPubSubWriter.class.getName());

  private static final String TOPIC = "bob-topic3";
  private static final String PROJECT = "customer-support-217822";

  GCPubSubWriter() {
  }

  public static void main(String... args) {

    GCPubSubWriter gcpsw = new GCPubSubWriter();


    log.log(Level.FINE, "J.U.L. ALL!!!");

    //  can we get this log into the
    log.log(Level.SEVERE, "J.U.L. SEVERE!!!");

    LOG2.trace("log 2  TRACE");
    LOG2.info("log 2 INFO");

    //    gcpsw.createTopic("bob-test-9");
    System.exit(3);
    gcpsw.listTopicsExample();
    //    gcpsw.run();

  }

  void createTopic(String tn) {

    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      TopicName topicName = TopicName.of(PROJECT, tn);
      Topic topic = topicAdminClient.createTopic(topicName);
      System.out.println("Created topic: " + topic.getName());
    } catch (IOException ex) {
      //LOG.error("bob Exception: {}", ex.getMessage(), ex);
      System.exit(3);
    }
  }

  void listTopicsExample() {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      ProjectName projectName = ProjectName.of(PROJECT);
      for (Topic topic : topicAdminClient.listTopics(projectName).iterateAll()) {
        System.out.println(topic.getName());
      }
      System.out.println("Listed all topics.");
    } catch (IOException ex) {
      //LOG.error("Exception: {}", ex.getMessage(), ex);
    }
  }

  void run() {
    Publisher publisher = null;
    try {
      publisher = Publisher.newBuilder(TOPIC).build();
      ByteString data = ByteString.copyFromUtf8("my-message");
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<String>() {
        public void onSuccess(String messageId) {
          System.out.println("published with message id: " + messageId);
        }

        public void onFailure(Throwable t) {
          System.out.println("failed to publish: " + t);
        }
      }, MoreExecutors.directExecutor());
      //...

    } catch (IOException ex) {
      //LOG.error("Exception: {}", ex.getMessage(), ex);

    } finally {
      if (publisher != null) {
        publisher.shutdown();
        try {
          publisher.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          //LOG.error("Exception: {}", ex.getMessage(), ex);

        }
      }
    }
  }
}
