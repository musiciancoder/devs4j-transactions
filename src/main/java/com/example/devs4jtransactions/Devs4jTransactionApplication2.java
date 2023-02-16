package com.example.devs4jtransactions;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Lo creó en seccion Probando el RestHighLevelClient

@SpringBootApplication
public class Devs4jTransactionApplication2 implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Devs4jTransactionApplication2.class);


    public static void main(String[] args) {
        SpringApplication.run(Devs4jTransactionsApplication.class, args);

    }

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void run(String... args) throws Exception {
        IndexRequest indexRequest = new IndexRequest("devs4j-transactions");
        indexRequest.id("44"); //este es el identificador del registro para postman por ejemplo
        indexRequest.source //este es el json que estamos pasando en forma asincrona, ya que esta clase la creamos pa a funcionara como clase principal del cliente
                ("{\"nombre\":\"Alex\"}", XContentType.JSON);

    IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT); //Lo indexa en forma asincrona

    log.info("Response id= {}", response.getId());


    }
}
