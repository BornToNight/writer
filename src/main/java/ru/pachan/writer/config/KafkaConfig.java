package ru.pachan.writer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import ru.pachan.writer.dto.WriterDto;
import ru.pachan.writer.service.writerDtoConsumer.WriterDtoConsumer;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${application.kafka.topic}")
    private String topicName;

    @Bean
    public ConsumerFactory<String, WriterDto> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 3);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3000);

        DefaultKafkaConsumerFactory<String, WriterDto> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props);
        kafkaConsumerFactory.setValueDeserializer(new JsonDeserializer<>(WriterDto.class, false));
        return kafkaConsumerFactory;
    }

    @Bean("listenerContainerFactory")
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, StringValue>> listenerContainerFactory(
    public ConcurrentKafkaListenerContainerFactory<String, WriterDto> listenerContainerFactory(
            ConsumerFactory<String, WriterDto> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, WriterDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setIdleBetweenPolls(1000);
        factory.getContainerProperties().setPollTimeout(1000);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("k-consumer-");
        executor.setConcurrencyLimit(10);
        ConcurrentTaskExecutor listenerTaskExecutor = new ConcurrentTaskExecutor(executor);
        factory.getContainerProperties().setListenerTaskExecutor(listenerTaskExecutor);
        return factory;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }

    @Bean
    public KafkaClient writerDtoConsumer(WriterDtoConsumer writerDtoConsumer) {
        return new KafkaClient(writerDtoConsumer);
    }

    @RequiredArgsConstructor
    public static class KafkaClient {
        private final WriterDtoConsumer writerDtoConsumer;

        @KafkaListener(topics = {"${application.kafka.topic}"}, containerFactory = "listenerContainerFactory")
        public void listen(@Payload List<WriterDto> writerDtoList) {
            writerDtoConsumer.accept(writerDtoList);
        }
    }

}