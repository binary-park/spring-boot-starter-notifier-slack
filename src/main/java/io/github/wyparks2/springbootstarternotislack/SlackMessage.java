package io.github.wyparks2.springbootstarternotislack;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class SlackMessage {

    private String title;

    private String text;
}
