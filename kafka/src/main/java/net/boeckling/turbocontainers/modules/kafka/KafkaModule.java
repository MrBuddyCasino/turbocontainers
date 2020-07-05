package net.boeckling.turbocontainers.modules.kafka;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.KafkaContainer;

public class KafkaModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(KafkaContainer.class);
    context.addLifecycleListener(new KafkaStateManager());
    context.addParameterProvider(new KafkaParameterProvider());
  }
}
