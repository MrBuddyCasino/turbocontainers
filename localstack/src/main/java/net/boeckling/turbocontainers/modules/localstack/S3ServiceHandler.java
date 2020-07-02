package net.boeckling.turbocontainers.modules.localstack;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ServiceHandler
  implements LifecycleListener<LocalStackContainer> {
  private AmazonS3 s3;
  private static final Map<String, Set<String>> BUCKETS_TO_PRESERVE = new HashMap<>();

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof LocalStackContainer;
  }

  @Override
  public void afterContainerInitialized(LocalStackContainer container) {
    Set<String> toPreserve = listBucketNames(container);
    BUCKETS_TO_PRESERVE.put(container.getContainerId(), toPreserve);
  }

  @NotNull
  private Set<String> listBucketNames(LocalStackContainer container) {
    return getS3Client(container)
      .listBuckets()
      .stream()
      .map(Bucket::getName)
      .collect(toSet());
  }

  @Override
  public void beforeEachTest(LocalStackContainer container) {
    Collection<String> toDelete = listBucketNames(container);
    Set<String> toPreserve = BUCKETS_TO_PRESERVE.getOrDefault(
      container.getContainerId(),
      emptySet()
    );
    toDelete.removeAll(toPreserve);
    toDelete.forEach(getS3Client(container)::deleteBucket);
  }

  private AmazonS3 getS3Client(LocalStackContainer container) {
    if (s3 == null) {
      s3 = createS3Client(container);
    }
    return s3;
  }

  static AmazonS3 createS3Client(LocalStackContainer container) {
    return AmazonS3ClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(S3))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build();
  }

  static S3Client createS3ClientV2(LocalStackContainer container) {
    return Common.configure(S3Client.builder(), container, S3).build();
  }
}
