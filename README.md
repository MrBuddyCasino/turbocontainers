# turbocontainers
Testcontainers on steroids.

## Description

Turbocontainers is a non-intrusive wrapper for Testcontainers.
The main goal is to make using a Testcontainer faster by 
keeping the container alive and instead of restarting, 
 reset the state between tests.

#### Example

Configure dependencies with the `junit5` module
plus the containers you want:

    net.boeckling.turbocontainers:junit5
    net.boeckling.turbocontainers:mongodb

Test class:
```
@TurboContainers
public class MongodbListenerTest {
  @TurboContainer
  final MongoDBContainer mongodb = MongodbConfiguration.CONTAINER;

  @Test
  void mongodb_shouldInitialize_whenStarting(MongoClient client) {
    MongoCollection<Document> collection = client
      .getDatabase(DB_NAME)
      .getCollection("collection");
    assertThat(collection.countDocuments()).isEqualTo(1L);
  }
}
```

Note how the `MongoClient` parameter was provided automatically.

### Container Lifecycle
A Turbocontainer has the following lifecycle:
* start
* optional initialization with state that is persistent for all subsequent tests
* execution of a single test
* reset of container state to what it was before the first test
* execution of the next test
* repeat until tests are finished

## Supported containers

Every container needs explicit support. Currently supported:

* postgresql
* mongodb
* localstack: S3, SNS, SQS
* kafka
* all transactional JdbcDatabaseContainers

## Container Initialization

You can use a Testcontainer directly, or use a convenience initializer wrapper.
It supports a `Consumer<Testcontainer>` or a `BiConsumer<Testcontainer, Client>`
initializer.

The second parameter in the `BiConsumer` is module-specific and can be a `DataSource` or a `MongoClient`, for example. 
It is automatically created and configured to target the respective container. 

Example with `BiConsumer`:
```
public class MongodbConfiguration {

  public static final int MONGO_PORT = 27017;
  public static final String DB_NAME = "database";

  public static final MongoDBContainer CONTAINER = Init
    .<MongoDBContainer, MongoClient>container(
      new MongoDBContainer()
        .withEnv("MONGO_INITDB_DATABASE", DB_NAME)
    )
    .with(
      (con, client) ->
        client
          .getDatabase(DB_NAME)
          .getCollection("collection")
          .insertOne(new Document("key", "value"))
    );
}
```
As `Consumer` without the second parameter:
```
public class MongodbConfiguration {

  public static final int MONGO_PORT = 27017;
  public static final String DB_NAME = "database";

  public static final MongoDBContainer CONTAINER = container(
      new MongoDBContainer()
        .withEnv("MONGO_INITDB_DATABASE", DB_NAME)
    )
    .with(MongodbConfiguration::initialize);

  // can also use a method reference instead of a lambda
  static void initialize(MongoDBContainer container) {
    String dbName = container.getEnvMap().get("MONGO_INITDB_DATABASE");
    MongoClient client = MongoClients.create(container.getReplicaSetUrl());
    client
      .getDatabase(dbName)
      .getCollection("collection")
      .insertOne(new Document("key", "value"));
  }
}
```

## Modules

### cli
Provides the `ShellScript` tool to easily run scripts in a container.

### jdbc
Provides the `DataSource` parameter type. 
Transactions are rolled-back at the end of each test. 
The underlying connection cannot be committed or closed.

### kafka
Provides the `KafkaConsumer` parameter type.
Topics are deleted between each test run.

### localstack
Supported services:

##### S3 
Deletes buckets between tests. 
Parameters: `AmazonS3`, `S3Client`

##### SNS 
Deletes topics, apps and endpoints between tests. 
Parameters: `AmazonSNS`, `SnsClient`

##### SQS
Deletes queues between tests.
Parameters: `AmazonSQS`, `SqsClient`

### postgres
Not needed for JDBC-only operations.
Provides a `PsqlScript` class to execute the psql cli tool:

        PsqlScript
          .of(Script.of(Paths.get("src/test/resources/psql.sql")))
          .runIn(container);