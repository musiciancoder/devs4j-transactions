package com.example.devs4jtransactions;

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

	@Autowired
	private KafkaTemplate<String, String>kafkaTemplate;

	private static  final Logger log = LoggerFactory.getLogger(Devs4jTransactionsApplication.class);

	@KafkaListener(topics="devs4j-transactions", groupId = "devs4jGroup",
			containerFactory = "kafkaListenerContainerFactory") //el mismo de la clase KafkaConfiguration
    public void listen(List<String>messages){
		for (String messsage:messages){
			log.info(messsage);
		}
	}

	//diez mil mensajes cada diez segundos
	@Scheduled(fixedRate = 10000)
	public void sendMessages(){
		for (int i = 0; i < 100000; i++) {
			kafkaTemplate.send("devs4j-transactions","key","Mensaje " + i);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Devs4jTransactionsApplication.class, args);
	}

}
