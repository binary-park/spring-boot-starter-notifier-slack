package io.github.wyparks2.autoconfigure.notifier.slack;

import org.springframework.util.CollectionUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

public class SlackNotifierMessageProcessor {
    private final Flux<SlackNotifierMessage> slackNotifierMessageFlux;
    private final SlackNotifierMessageSender slackNotifierMessageSender;
    private final SlackNotifierProperties slackNotifierProperties;

    private Disposable disposable;

    public SlackNotifierMessageProcessor(Flux<SlackNotifierMessage> slackNotifierMessageFlux,
                                         SlackNotifierMessageSender slackNotifierMessageSender,
                                         SlackNotifierProperties slackNotifierProperties) {
        this.slackNotifierMessageFlux = slackNotifierMessageFlux;
        this.slackNotifierMessageSender = slackNotifierMessageSender;
        this.slackNotifierProperties = slackNotifierProperties;
    }

    @PostConstruct
    public void init() {
        final int bufferSize = slackNotifierProperties.getBufferSize();
        final int period = slackNotifierProperties.getPeriod();

        this.disposable = slackNotifierMessageFlux
                .bufferTimeout(bufferSize, Duration.ofSeconds(period), Schedulers.elastic())
                .filter(item -> !CollectionUtils.isEmpty(item))
                .subscribe(slackNotifierMessageSender::send);
    }

    @PreDestroy
    public void destroy() {
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }
}
