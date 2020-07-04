package net.boeckling.turbocontainers.modules.mongodb;

import com.mongodb.client.MongoClient;
import java.util.function.BiConsumer;
import net.boeckling.turbocontainers.api.init.Init;
import org.bson.Document;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;

public class MongodbConfiguration
  implements BiConsumer<MongoDBContainer, MongoClient> {
  public static final int MONGO_PORT = 27017;
  public static final String DB_NAME = "database";
  public static final MongoDBContainer CONTAINER = Init
    .<MongoDBContainer, MongoClient>container(
      new MongoDBContainer()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")
        .withExposedPorts(MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DB_NAME)
        .withCommand("--bind_ip_all")
        .dependsOn()
    )
    .with(new MongodbConfiguration());

  @Override
  public void accept(MongoDBContainer container, MongoClient mongoClient) {
    mongoClient
      .getDatabase(DB_NAME)
      .getCollection("collection")
      .insertOne(new Document("key", "value"));
  }
}
