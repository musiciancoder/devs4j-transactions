package com.example.devs4jtransactions;

import com.example.devs4jtransactions.models.Devs4jTransaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

//NOTA: si hay dudas con las anotaciones ver los comentarios en proyecto curso_kafka_spring

@SpringBootApplication
@EnableScheduling
public class Devs4jTransactionsApplication {

	//diez mil mensajes cada quince segundos

	//Lo comentó en seccion Probando el RestHighLevelClient


	@Autowired
	private KafkaTemplate<String, String>kafkaTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestHighLevelClient client;

	private static  final Logger log = LoggerFactory.getLogger(Devs4jTransactionsApplication.class);

	//consumer
	@KafkaListener(topics="devs4j-transactions", groupId = "devs4jGroup",
			containerFactory = "kafkaListenerContainerFactory") //el mismo de la clase KafkaConfiguration
    public void listen(List<ConsumerRecord>messages) throws JsonProcessingException { //inicialmente estaba como List<ConsumerRecord>messages

//		for (String messsage:messages){ //lo comentó en  Generando transacciones de ejemplo con Java Faker
//			log.info(message);


		for(ConsumerRecord<String,String>message:messages){
	//		Devs4jTransaction transaction = mapper.readValue(message.value(), Devs4jTransaction.class); //por ahora lo dejó comentado TODO
	//		log.info("Partition= {} Offset ={} Key={} Message={}", message.partition(), message.offset(), message.key(), message.value()); // Lo comentó en seccion Integrando cliente de Elasticsearch al consumer de Kafka
	//el siguiente código lo agregó en seccion  Integrando cliente de Elasticsearch al consumer de Kafka
		   IndexRequest indexRequest= buildIndexRequest(
				   String.format("%s-%s%s", message.partition(), message.key(), message.offset()), message.value());
		client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
			@Override
			public void onResponse(IndexResponse indexResponse) {
				log.debug("successful request");
			}

			@Override
			public void onFailure(Exception e) {
				log.debug("error storing the message {}", e);

			}
		});
		}
	}

	private IndexRequest buildIndexRequest (String key, String value){
      IndexRequest request= new IndexRequest("devs4j-transactions");
	  request.id(key);
	  request.source(value);
	  return request;
	}


	//producer
	@Scheduled(fixedRate = 15000)
	public void sendMessages() throws JsonProcessingException { //Propagacion de excepcion para mapper.writeValueAsString
		Faker faker = new Faker();
		for (int i = 0; i < 100000; i++) {

			Devs4jTransaction transaction = new Devs4jTransaction();
			transaction.setUsername(faker.name().username());
			transaction.setNombre(faker.name().firstName());
			transaction.setApellido(faker.name().lastName());
			transaction.setMonto(faker.number().randomDouble(4, 0, 2000000));
		//	kafkaTemplate.send("devs4j-transactions","key","Mensaje " + i); //lo comentó en sección  Generando transacciones de ejemplo con Java Faker
			kafkaTemplate.send("devs4j-transactions", transaction.getUsername(),mapper.writeValueAsString(transaction));
		}
	}



	public static void main(String[] args) {
		SpringApplication.run(Devs4jTransactionsApplication.class, args);
	}

}
