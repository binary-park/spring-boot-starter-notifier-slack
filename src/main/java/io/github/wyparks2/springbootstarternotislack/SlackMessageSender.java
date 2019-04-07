package io.github.wyparks2.springbootstarternotislack;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class SlackMessageSender {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(1000))
            .setReadTimeout(Duration.ofMillis(1000))
            .build();

    public void send(List<SlackMessage> slackMessages) {

        System.out.println(slackMessages.size());
        System.out.println(slackMessages.toString());
        System.out.println("-----------------------------------");
        Message message = new Message();
        for (SlackMessage slackMessage : slackMessages) {
            message.add(slackMessage);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    public class Message {

        private String text = "노티가 발행되었습니다.";

        private List<Attachment> attachments = new ArrayList<>();

        public void add(SlackMessage slackMessage) {
            attachments.add(new Attachment(slackMessage.getTitle(), slackMessage.getText(), AttachmentColorType.GRAY));
        }
    }

    @Getter
    @Setter
    private class Attachment {

        private String title;

        private String text;

        private AttachmentColorType color;

        public Attachment(String title, String text, AttachmentColorType color) {
            this.title = title;
            this.text = text;
            this.color = color;
        }
    }

    public enum AttachmentColorType {

        BLACK("000000"),
        GRAY("B4B4B4"),
        RED("B90000"),
        BLUE("00BFFF"),
        GREEN("006400"),
        YELLOW("FFD732"),
        PINK("FF6EED");

        private String rgb;

        AttachmentColorType(String rgb) {
            this.rgb = rgb;
        }
    }
}
