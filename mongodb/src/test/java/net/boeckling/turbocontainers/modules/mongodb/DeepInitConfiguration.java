package net.boeckling.turbocontainers.modules.mongodb;

import static net.boeckling.turbocontainers.api.init.Init.container;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import net.boeckling.turbocontainers.api.init.InitializerContext;
import org.bson.Document;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;

public class DeepInitConfiguration {
  public static final int MONGO_PORT = 27017;
  public static final String DB1_NAME = "database_1";
  public static final String DB2_NAME = "database_2";
  public static final String DB3_NAME = "database_3";

  public static final MongoDBContainer CONTAINER_1 = container(
      new MongoDBContainer()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")
        .withExposedPorts(MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DB1_NAME)
        .withCommand("--bind_ip_all")
    )
    .with(DeepInitConfiguration::initialize);
  public static final MongoDBContainer CONTAINER_2 = container(
      new MongoDBContainer()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")
        .withExposedPorts(MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DB2_NAME)
        .withCommand("--bind_ip_all")
        .dependsOn(CONTAINER_1)
    )
    .with(DeepInitConfiguration::initialize);
  public static final MongoDBContainer CONTAINER_3 = container(
      new MongoDBContainer()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")
        .withExposedPorts(MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DB3_NAME)
        .withCommand("--bind_ip_all")
        .dependsOn(CONTAINER_2)
    )
    .with(DeepInitConfiguration::initialize);

  static void initialize(InitializerContext<MongoDBContainer> ctx) {
    String dbName = ctx.container().getEnvMap().get("MONGO_INITDB_DATABASE");
    ctx
      .client(MongoClient.class)
      .getDatabase(dbName)
      .getCollection("collection")
      .insertOne(new Document("key", "value"));
  }

  static MongoClient createMongoClient(MongoDBContainer container) {
    return MongoClients.create(container.getReplicaSetUrl());
  }
}
