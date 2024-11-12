package com.ingsis.jcli.snippets.consumers;

import static com.ingsis.jcli.snippets.consumers.DeserializerUtil.deserializeIntoSnippetStatusUpdate;

import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.common.responses.SnippetStatusUpdateProduct;
import com.ingsis.jcli.snippets.services.SnippetService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Generated
@Profile("!test")
@Slf4j
@Component
public class SnippetStatusUpdateConsumer extends RedisStreamConsumer<String> {

  private final SnippetService snippetService;

  @Autowired
  public SnippetStatusUpdateConsumer(
      @Value("${snippet_status_update.key}") String streamKey,
      @Value("${snippet_status_update.groups.product}") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      SnippetService snippetService) {
    super(streamKey, groupId, redis);
    this.snippetService = snippetService;
  }

  @NotNull
  @Override
  protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofMillis(10000))
        .targetType(String.class)
        .build();
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, String> objectRecord) {
    String statusUpdate = objectRecord.getValue();
    if (statusUpdate == null) {
      return;
    }
    SnippetStatusUpdateProduct snippetStatusUpdateProduct =
        deserializeIntoSnippetStatusUpdate(statusUpdate);

    log.info(
        "Update snippet "
            + snippetStatusUpdateProduct.getSnippetId()
            + " status to "
            + snippetStatusUpdateProduct.getStatus());

    if (snippetStatusUpdateProduct.getOperation().equals("format")) {
      snippetService.updateFormattingStatus(
          snippetStatusUpdateProduct.getStatus(), snippetStatusUpdateProduct.getSnippetId());
    } else if (snippetStatusUpdateProduct.getOperation().equals("lint")) {
      snippetService.updateLintingStatus(
          snippetStatusUpdateProduct.getStatus(), snippetStatusUpdateProduct.getSnippetId());
    }
  }
}
