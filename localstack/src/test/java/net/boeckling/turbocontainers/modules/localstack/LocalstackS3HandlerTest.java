package net.boeckling.turbocontainers.modules.localstack;

import static net.boeckling.turbocontainers.modules.localstack.LocalstackConfiguration.INIT_BUCKET_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.s3.S3Client;

@TurboContainers
public class LocalstackS3HandlerTest {
  @TurboContainer
  final LocalStackContainer container = LocalstackConfiguration.CONTAINER;

  @Test
  @Order(1)
  void s3client_shouldCreateBucket_always(AmazonS3 s3) {
    Bucket bucket = s3.createBucket("test-bucket");
    assertThat(s3.doesBucketExistV2(bucket.getName())).isTrue();
  }

  @Test
  @Order(2)
  void module_shouldDeleteBucket_whenCreatedInPreviousTest(AmazonS3 s3) {
    assertThat(s3.doesBucketExistV2("test-bucket")).isFalse();
  }

  @Test
  @Order(3)
  void initialData_shouldBePresent_always(AmazonS3 s3) {
    assertThat(s3.doesBucketExistV2(INIT_BUCKET_NAME)).isTrue();
  }

  @Test
  @Order(3)
  void clientV2_shouldQueryData_successfully(S3Client s3) {
    assertThat(s3.getBucketAcl(b -> b.bucket(INIT_BUCKET_NAME)).hasGrants())
      .isTrue();
  }
}
