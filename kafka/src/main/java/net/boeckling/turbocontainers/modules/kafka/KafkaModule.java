package net.boeckling.turbocontainers.modules.kafka;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;

public class KafkaModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new KafkaListener());
    context.addParameterProvider(new KafkaParameterProvider());
  }
}
