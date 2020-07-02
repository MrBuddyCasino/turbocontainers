package net.boeckling.turbocontainers.modules.mongodb;

import net.boeckling.turbocontainers.modules.cli.Script;

public class MongoScript {
  public static final Script MONGO = Script.of("mongo");

  public static Script of(Script script) {
    return MONGO.concat(script);
  }
}
