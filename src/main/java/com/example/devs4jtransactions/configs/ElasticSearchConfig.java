package com.example.devs4jtransactions.configs;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//clase  cliente de ElasticSearch para conectarnos hacia nuestro dominio
@Configuration
public class ElasticSearchConfig {

    @Bean(destroyMethod = "close") //para liberar recursos, esto es lo mismo que poner client.close() al final
    public RestHighLevelClient createClient() {

        //Seguridad
        final CredentialsProvider credentialsProvider=new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("devs4j","HolaAmigosDeDevs4j")); //"user","password" TODO PREGUNTAR A QUE CREDENCIALES DE AWS CORRESPONDE ESTO

        //Creacion cliente de ElasticSearch
        RestHighLevelClient client =new RestHighLevelClient(RestClient.builder(new HttpHost(
               // "localhost", 9200, "http")));
                "search-devs4j-wjugggfggdgdgsm45mmf.us.east-1.es.amazon.com",443,"https")) //el primer parametro se saca de Punto de enlace, pero sin el http y sin el endpoint del final; se le pone 443 pq segun el era https
                .setHttpClientConfigCallback((config)->config.setDefaultCredentialsProvider(credentialsProvider)));
        return  client;
    }
}
