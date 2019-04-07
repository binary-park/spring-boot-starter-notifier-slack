package io.github.wyparks2.autoconfigure.notifier.slack;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = "notifier.slack")
public class SlackNotifierProperties {
    private boolean enabled;

    @NotBlank
    private String webHookUrl;

    private String channel;

    private int bufferSize;

    private int period;
}
