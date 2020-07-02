package net.boeckling.turbocontainers.modules.mongodb;

import net.boeckling.turbocontainers.events.LifecycleListener;
import net.boeckling.turbocontainers.modules.cli.Script;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbListener implements LifecycleListener<MongoDBContainer> {
  private final Script mongoDump = Script.of("mongodump");
  private final Script mongoRestore = Script.of("mongorestore", "--dir=dump");

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof MongoDBContainer;
  }

  @Override
  public void afterContainerInitialized(MongoDBContainer container) {
    mongoDump.runIn(container);
  }

  @Override
  public void beforeEachTest(MongoDBContainer container) {
    mongoRestore.runIn(container);
  }
}
