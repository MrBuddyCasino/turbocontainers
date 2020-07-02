package net.boeckling.turbocontainers.modules.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.assertj.core.util.Streams;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

@TurboContainers
public class DeepInitTest {
  @TurboContainer
  final MongoDBContainer mongodb = DeepInitConfiguration.CONTAINER_3;

  @Test
  void container_shouldStartItsDependencies_whenStarting() {
    assertThat(DeepInitConfiguration.CONTAINER_1.isRunning()).isTrue();
    assertThat(DeepInitConfiguration.CONTAINER_2.isRunning()).isTrue();
    assertThat(DeepInitConfiguration.CONTAINER_3.isRunning()).isTrue();
  }

  @Test
  void containerDependencies_shouldRunInitialization_whenStarting() {
    runAssertions(
      DeepInitConfiguration.CONTAINER_1,
      DeepInitConfiguration.DB1_NAME
    );
    runAssertions(
      DeepInitConfiguration.CONTAINER_2,
      DeepInitConfiguration.DB2_NAME
    );
    runAssertions(
      DeepInitConfiguration.CONTAINER_3,
      DeepInitConfiguration.DB3_NAME
    );
  }

  private void runAssertions(MongoDBContainer container, String dbName) {
    MongoClient client = DeepInitConfiguration.createMongoClient(container);
    assertThat(
        Streams
          .stream(client.listDatabaseNames())
          .anyMatch(n -> n.equals(dbName))
      )
      .isTrue();

    MongoCollection<Document> collection = client
      .getDatabase(dbName)
      .getCollection("collection");
    assertThat(collection.countDocuments()).isEqualTo(1L);
  }
}
