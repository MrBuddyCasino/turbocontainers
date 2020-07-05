package net.boeckling.turbocontainers.modules.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

@TurboContainers
public class ParamResolutionTest {
  @TurboContainer("C1")
  final MongoDBContainer mongodb1 = DeepInitConfiguration.CONTAINER_1;

  @TurboContainer("C2")
  final MongoDBContainer mongodb2 = DeepInitConfiguration.CONTAINER_2;

  @Test
  void ambiguousParameters_shouldBeUnambiguous_whenNamed(
    @TurboContainer("C1") MongoClient client
  ) {
    assertThat(client).isNotNull();
  }
}
