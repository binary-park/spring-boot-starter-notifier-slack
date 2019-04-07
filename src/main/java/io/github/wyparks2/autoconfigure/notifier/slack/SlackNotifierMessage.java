package io.github.wyparks2.autoconfigure.notifier.slack;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class SlackNotifierMessage {
    private String title;
    private String text;
}
