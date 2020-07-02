package net.boeckling.turbocontainers.modules.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import net.boeckling.turbocontainers.parameter.ExecutionEnvironment;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import net.boeckling.turbocontainers.parameter.SimpleParamProvider;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbParamProvider extends SimpleParamProvider {

  public MongodbParamProvider() {
    super(MongoClient.class, MongoDBContainer.class);
  }

  @Override
  public Object resolveParameter(
    ParameterDescriptor param,
    ExecutionEnvironment env
  ) {
    MongoDBContainer mongoDBContainer = tryCast(
      MongoDBContainer.class,
      env.getContainer()
    );
    return MongoClients.create(mongoDBContainer.getReplicaSetUrl());
  }
}
