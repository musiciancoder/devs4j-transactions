package com.example.devs4jtransactions.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

//configuracion consumer
    @Bean
    public Map<String, Object> consumerProps() {
        Map<String, Object>props=new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "CopiarYPegarAquiELPlainTextDelClusterAWS"); //en un proyecto corporativo aqui iria un servidor de configuraciones de la empresa (amazon tiene uno, pero hay que pagarlo).
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    //configuracion producer
    private Map<String, Object> producerProps() { Map<String, Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    //template del producer
    @Bean
    public KafkaTemplate<String, String> createTemplate() { Map<String, Object>senderProps= producerProps();
        ProducerFactory<String, String> pf= new DefaultKafkaProducerFactory<String, String>(senderProps);
        KafkaTemplate<String, String> template=new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public ObjectMapper mapper(){
        return new ObjectMapper();
    }

    //factory para el consumer
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    //container factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(50); //tan solo con esta linea definimos 50 consumers (o sea 50 threads)
        factory.setBatchListener(true); //para trabajar con batch listeners
        return factory;
    }



}
