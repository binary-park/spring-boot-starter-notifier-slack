package io.github.wyparks2.springbootstarternotislack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SlackConfig {

    @Bean
    public EmitterProcessor<SlackMessage> slackMessageEmitterProcessor() {
        return EmitterProcessor.create();
    }

    @Bean
    public FluxSink<SlackMessage> slackMessageSink(EmitterProcessor<SlackMessage> slackMessageEmitterProcessor) {
        return slackMessageEmitterProcessor.sink(FluxSink.OverflowStrategy.DROP);
    }

    @Bean
    public Flux<SlackMessage> slackMessageFlux(EmitterProcessor<SlackMessage> slackMessageEmitterProcessor) {
        return slackMessageEmitterProcessor.publishOn(Schedulers.elastic());
    }
}

