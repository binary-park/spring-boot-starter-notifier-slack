package io.github.wyparks2.autoconfigure.notifier.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SlackNotifierMessageSender {
    private final SlackNotifierProperties properties;
    private final RestTemplate restTemplate;

    public SlackNotifierMessageSender(SlackNotifierProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(1000))
                .build();
    }

    public void send(List<SlackNotifierMessage> slackNotifierMessages) {
        Message message = makeMessage(slackNotifierMessages);

        requestSendMessage(message);
    }

    private void requestSendMessage(Message message) {
        int maxRetry = 3;
        while (maxRetry -- > 0) {
            try {
                sleep();
                ResponseEntity<String> response = restTemplate.postForEntity(properties.getWebHookUrl(), message, String.class);
                if (response.getStatusCode().is2xxSuccessful())
                    return;
            } catch (Exception e) {
                log.error("오류가 발생하였습니다. {}", e);
            }
        }

        log.error("메시지 발송을 실패하였습니다. {}", message);
    }

    private Message makeMessage(List<SlackNotifierMessage> slackNotifierMessages) {
        Message message = new Message();
        message.setChannel(properties.getChannel());
        for (SlackNotifierMessage slackNotifierMessage : slackNotifierMessages) {
            message.addAttachment(slackNotifierMessage);
        }

        return message;
    }

    /**
     * 슬랙 발송을 1초 단위로 1번만 발송 되도록 한다.
     * 순간적으로 다량의 메시지를 발송하면 발송 실패 혹은 차단된다.
     */
    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("{}", e);
        }
    }

    @Setter
    private class Message {
        private final String text = String.format("메시지가 %d건 도착하였습니다.", this.attachments.size());
        private String channel;
        private final List<Attachment> attachments = new ArrayList<>();

        public void addAttachment(SlackNotifierMessage slackNotifierMessage) {
            attachments.add(
                new Attachment(slackNotifierMessage.getTitle(), slackNotifierMessage.getText())
            );
        }
    }

    @Getter
    @Setter
    private class Attachment {
        private String title;

        @JsonProperty("title_link")
        private String titleLink;

        private String text;
        private AttachmentColorType color = AttachmentColorType.GRAY;

        public Attachment(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public Attachment(String title, String text,AttachmentColorType color) {
            this.title = title;
            this.text = text;
            this.color = color;
        }
    }

    private enum AttachmentColorType {
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
