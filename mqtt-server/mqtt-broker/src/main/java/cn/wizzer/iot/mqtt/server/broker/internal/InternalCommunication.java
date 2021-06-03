

package cn.wizzer.iot.mqtt.server.broker.internal;

import cn.wizzer.iot.mqtt.server.broker.cluster.RedisCluster;
import cn.wizzer.iot.mqtt.server.broker.config.BrokerProperties;
import cn.wizzer.iot.mqtt.server.broker.service.KafkaService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息转发，基于kafka
 */
@IocBean
public class InternalCommunication {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalCommunication.class);
    @Inject
    private BrokerProperties brokerProperties;
    @Inject
    private KafkaService kafkaService;
    @Inject
    private RedisCluster redisCluster;

    public void internalSend(InternalMessage internalMessage) {
        String processId = Lang.JdkTool.getProcessId("0");
        //broker唯一标识 mqttwk.broker.id
        internalMessage.setBrokerId(brokerProperties.getId());
        internalMessage.setProcessId(processId);
        //如果开启kafka消息转发
        if (brokerProperties.getKafkaBrokerEnabled()) {
            kafkaService.send(internalMessage);
        }
        //如果开启集群功能
        if (brokerProperties.getClusterEnabled()) {
            redisCluster.sendMessage(internalMessage);
        }
    }
}
