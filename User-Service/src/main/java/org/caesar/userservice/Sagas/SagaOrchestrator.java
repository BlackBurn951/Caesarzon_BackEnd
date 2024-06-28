package org.caesar.userservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final SagaProducer sagaProducer;

    public void orchestrateSaga(int topicNumber){

        switch (topicNumber){
            case 0 -> sagaProducer.sendMessage("topic1", "message1");

        }
    }
    
    @KafkaListener(topics = "ban-unban-user-response", groupId = "saga-consumers")
    public void handleBanUnbanUserResponse(String message) {
        // Logica per gestire la risposta
        System.out.println("Received response: " + message);
        // Procedi con la prossima operazione
        sagaProducer.sendMessage("delete-review", "Start delete review");
    }

    @KafkaListener(topics = "delete-review-response", groupId = "saga-consumers")
    public void handleDeleteReviewResponse(String message) {
        // Logica per gestire la risposta
        System.out.println("Received response: " + message);
        // Procedi con la prossima operazione o termina il SAGA
    }
}
