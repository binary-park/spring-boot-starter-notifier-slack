package io.github.wyparks2.springbootstarternotislack;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

@Component
public class SlackMessageProcessor {

    private final Flux<SlackMessage> slackMessageFlux;

    private final SlackMessageSender slackMessageSender;

    private Disposable disposable;

    public SlackMessageProcessor(Flux<SlackMessage> slackMessageFlux, SlackMessageSender slackMessageSender) {
        this.slackMessageFlux = slackMessageFlux;
        this.slackMessageSender = slackMessageSender;
    }

    @PostConstruct
    public void init() {
        this.disposable = slackMessageFlux.bufferTimeout(100, Duration.ofSeconds(10), Schedulers.elastic())
                .filter(item -> !CollectionUtils.isEmpty(item))
                .subscribe(slackMessageSender::send);
    }

    @PreDestroy
    public void destroy() {
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }
}
