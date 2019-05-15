package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.constant.Constants;
import com.ivan.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * <h1>消费 Kafka 中的 PassTemplate</h1>
 * @Author Ivan 16:30
 * @Description TODO
 */
@Slf4j
@Component
public class ConsumerPassTemplate {

    /** 相关的 HBase 服务 */
    private final IHBasePassService passService;

    @Autowired
    public ConsumerPassTemplate(IHBasePassService passService) {
        this.passService = passService;
    }

    /**
     * 先解析Kafka中传递过来的passTemplate并序列化为Java 对象后，调用相应的服务处理
     * @param passTemplate
     * @param key
     * @param partition
     * @param topic
     */
    @KafkaListener(topics = {Constants.TEMPLATE_TOPIC})
    public void receive(@Payload String passTemplate,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        log.info("Consumer Receive PassTemplate:{}",passTemplate);

        PassTemplate pt;
        try {
            pt = JSON.parseObject(passTemplate,PassTemplate.class);
        }catch (Exception ex){
            log.error("Parse PassTemplate Error:{}",ex.getMessage());
            return;
        }
        if (passService.dropPassTemplateToHBase(pt)){

            log.info("DropPassTemplateToHBase: Successful!");
        }else {
            log.error("DropPassTemplateToHBase: Failure!");
        }

    }
}
