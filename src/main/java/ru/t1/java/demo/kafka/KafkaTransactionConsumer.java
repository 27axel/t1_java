package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionConsumer {
    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.transaction-id}",
                topics = "${t1.kafka.topic.transactions}",
                containerFactory = "kafkaTransactionListenerContainerFactory")
    public void listener(@Payload List<TransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            List<Transaction> transactions = messageList.stream()
                    .map(TransactionMapper::toEntity)
                    .toList();
            transactionService.registerAccounts(transactions);
        } finally {
            ack.acknowledge();
        }


        log.debug("Transaction consumer: записи обработаны");
    }
}
