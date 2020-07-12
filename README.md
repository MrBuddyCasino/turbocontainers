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

It is possible that the container state is reset completely between each test,
with the initialization being re-run, or that the 
initialization step is executed only once. This depends on how easy it is
to implement correctly.

## Supported containers

Every container needs explicit support. Currently supported:

* MariaDB
* MS SQL Server
* MySQL
* PostgreSQL
* Prestosql
* MongoDB
* Localstack: S3, SNS, SQS
* Kafka
* Cassandra
* Most containers based on `JdbcDatabaseContainer` should work 

## Container Initialization

You can use a Testcontainer directly, or use a convenience initializer wrapper.
It supports a `Consumer<InitializerContext>` initializer.

Example:
```
public class MongodbConfiguration {
  public static final int MONGO_PORT = 27017;
  public static final String DB_NAME = "database";
  public static final MongoDBContainer CONTAINER = Init
    .container(
      new MongoDBContainer()
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")
        .withExposedPorts(MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DB_NAME)
        .withCommand("--bind_ip_all")
    )
    .with(
      ctx ->
        ctx
          .client(MongoClient.class)
          .getDatabase(DB_NAME)
          .getCollection("collection")
          .insertOne(new Document("key", "value"))
    );
}
```

Note how the `InitializerContext` provides a `.client(<ClientClass>)` method to generate a
client for the current container.

## Modules

### cli
Provides the `ShellScript` tool to easily run scripts in a container.

### jdbc
Provides the `DataSource` parameter type. 
Transactions are rolled-back at the end of each test. 
The underlying connection cannot be committed or closed.

### kafka
Provides the `KafkaConsumer` parameter type.
Deletes topics between each test run.

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

### mongodb
Restores a snapshot between tests.
Parameters: `MongoClient`

Provides the `MongoScript` tool to run the `mongo` cli.

### postgres
Not needed for JDBC-only operations.

Provides a `PsqlScript` class to execute the psql cli tool:

        PsqlScript
          .of(Script.of(Paths.get("src/test/resources/psql.sql")))
          .runIn(container);
          
### cassandra
Deletes non-system keyspaces between tests.

Provides `CqlshScript`.

### Clickhouse
Deletes databases, truncates `default` database.