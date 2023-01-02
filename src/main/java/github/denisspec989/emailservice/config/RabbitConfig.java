package github.denisspec989.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String QUEUE_MESSAGES_DLQ = "deadMessageEmailProvider";
    public static final String DLX_EXCHANGE_MESSAGES = "deadExchangeEmailProvider.dlx";

    @Value("${rabbit.host}")
    private String rabbitHost;

    @Value("${concurrentConsumers}")
    private Integer concurrentConsumers;
    @Value("${maxConcurrentConsumers}")
    private Integer maxConcurrentConsumers;
    @Value("${prefetchCount}")
    private Integer prefetchCount;

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_DLQ).build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE_MESSAGES);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    public Queue sendEmail() {
        return QueueBuilder.durable("sendEmail")
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_MESSAGES)
                .build();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(rabbitHost);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory =
                new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        rabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        rabbitListenerContainerFactory.setConcurrentConsumers(concurrentConsumers);
        rabbitListenerContainerFactory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        rabbitListenerContainerFactory.setPrefetchCount(prefetchCount);
        rabbitListenerContainerFactory.setDefaultRequeueRejected(false);
        rabbitListenerContainerFactory.setMessageConverter(messageConverter());
        return rabbitListenerContainerFactory;
    }
}
