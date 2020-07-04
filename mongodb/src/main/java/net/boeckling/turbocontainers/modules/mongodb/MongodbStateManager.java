package net.boeckling.turbocontainers.modules.mongodb;

import net.boeckling.turbocontainers.modules.cli.Script;
import net.boeckling.turbocontainers.state.InitOnceStateManager;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbStateManager
  implements InitOnceStateManager<MongoDBContainer> {
  private final Script mongoDump = Script.of("mongodump");
  private final Script mongoRestore = Script.of("mongorestore", "--dir=dump");

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof MongoDBContainer;
  }

  @Override
  public void takeSnapshot(MongoDBContainer container) {
    mongoDump.runIn(container);
  }

  @Override
  public void restoreSnapshot(MongoDBContainer container) {
    mongoRestore.runIn(container);
  }
}
