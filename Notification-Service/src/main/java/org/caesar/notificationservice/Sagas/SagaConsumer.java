//package org.caesar.notificationservice.Sagas;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class SagaConsumer {
//
//    private final SagaProducer sagaProducer;
//
//    @KafkaListener(topics = "ban-unban-user", groupId = "saga-consumers")
//    public void consumeBanUnbanUser(String message) {
//        // Logica per gestire il messaggio ricevuto
//        System.out.println("Processing message: " + message);
//        // Esegui l'operazione di ban/unban
//        sagaProducer.sendMessage("ban-unban-user-response", "Ban/unban completed");
//    }
//
//    @KafkaListener(topics = "delete-review", groupId = "saga-consumers")
//    public void consumeDeleteReview(String message) {
//        // Logica per gestire il messaggio ricevuto
//        System.out.println("Processing message: " + message);
//        // Esegui l'operazione di eliminazione della recensione
//        sagaProducer.sendMessage("delete-review-response", "Delete review completed");
//    }
//}
