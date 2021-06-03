package cn.wizzer.iot.mqtt.server.broker.service;

import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.broker.internal.InternalMessage;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@IocBean
public class KafkaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
    @Inject
    private KafkaProducer kafkaProducer;
    @Inject
    private BrokerProperties brokerProperties;

    @Async
    @SuppressWarnings("unchecked")
    public void send(InternalMessage internalMessage) {
        try {
            //消息体转换为Hex字符串进行转发
            ProducerRecord<String, String> data = new ProducerRecord<>(brokerProperties.getProducerTopic(), internalMessage.getTopic(), JSONObject.toJSONString(internalMessage));
            kafkaProducer.send(data,
                    new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                                LOGGER.error(e.getMessage(), e);
                            } else {
                                LOGGER.info("The offset of the record we just sent is: " + metadata.offset());
                            }
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("kafka没有连接成功..");
        }
    }

}
