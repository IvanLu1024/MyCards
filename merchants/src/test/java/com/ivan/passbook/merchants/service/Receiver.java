package com.ivan.passbook.merchants.service;

import com.ivan.passbook.merchants.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @Author Ivan 9:27
 * @Description TODO
 */
@Slf4j
public class Receiver {

    @KafkaListener(topics = Constants.TEMPLATE_TOPIC)
    public static void listen(@Payload String msg){
      log.info("Consumer receive :{}",msg);

    }
}
