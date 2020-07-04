package net.boeckling.turbocontainers.modules.mongodb;

import static net.boeckling.turbocontainers.modules.mongodb.MongodbConfiguration.DB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.bson.Document;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

@TurboContainers
public class MongodbStateManagerTest {
  @TurboContainer
  final MongoDBContainer mongodb = MongodbConfiguration.CONTAINER;

  @Test
  @Order(1)
  void mongodb_shouldInitialize_whenStarting(MongoClient client) {
    MongoCollection<Document> collection = client
      .getDatabase(DB_NAME)
      .getCollection("collection");
    assertThat(collection.countDocuments()).isEqualTo(1L);

    client
      .getDatabase(DB_NAME)
      .getCollection("collection2")
      .insertOne(new Document("key", "value"));
  }

  @Test
  @Order(2)
  void mongodb_shouldDiscardDataFromPreviousTest_whenDbWasChanged(
    MongoClient client
  ) {
    MongoIterable<String> collectionNames = client
      .getDatabase("database")
      .listCollectionNames();
    assertThat(collectionNames).doesNotContain("collection2");
  }
}
