package io.github.wyparks2.autoconfigure.notifier.slack;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnProperty(name = "notifier.slack.enabled", havingValue = "true")
@EnableConfigurationProperties(SlackNotifierProperties.class)
public class SlackNotifierAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public EmitterProcessor<SlackNotifierMessage> slackNotifierMessageEmitterProcessor() {
        return EmitterProcessor.create();
    }

    @ConditionalOnMissingBean
    @Bean
    public FluxSink<SlackNotifierMessage> slackNotifierMessageSink(
            EmitterProcessor<SlackNotifierMessage> slackNotifierMessageEmitterProcessor) {
        return slackNotifierMessageEmitterProcessor.sink(FluxSink.OverflowStrategy.DROP);
    }

    @ConditionalOnMissingBean
    @Bean
    public Flux<SlackNotifierMessage> slackNotifierMessageFlux(
            EmitterProcessor<SlackNotifierMessage> slackNotifierMessageEmitterProcessor) {
        return slackNotifierMessageEmitterProcessor.publishOn(Schedulers.elastic());
    }

    @ConditionalOnMissingBean
    @Bean
    public SlackNotifierMessageSender slackNotifierMessageSender(SlackNotifierProperties slackNotifierProperties) {
        return new SlackNotifierMessageSender(slackNotifierProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public SlackNotifierMessageProcessor slackSenderMessageProcessor(
            Flux<SlackNotifierMessage> slackNotifierMessageFlux,
            SlackNotifierMessageSender slackNotifierMessageSender,
            SlackNotifierProperties slackNotifierProperties) {
        return new SlackNotifierMessageProcessor(slackNotifierMessageFlux, slackNotifierMessageSender, slackNotifierProperties);
    }
}
