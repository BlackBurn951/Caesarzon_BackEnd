package org.caesar.notificationservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

//    @Bean
//    public NewTopic banUserTopic() {
//        return TopicBuilder.name("ban-user").partitions(1).replicas(1).build();
//    }
//    @Bean
//    public NewTopic sbanUserTopic() {
//        return TopicBuilder.name("sban-user").partitions(1).replicas(1).build();
//    }



}
