package io.github.wyparks2.springbootstarternotislack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.FluxSink;

@SpringBootApplication
public class Application {

    @Autowired
    public FluxSink<SlackMessage> slackMessageSink;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            SlackMessage slackMessage = new SlackMessage("title", "text");

            while (true) {
                slackMessageSink.next(slackMessage);
//                Thread.sleep(500);
            }
        };
    }
}
