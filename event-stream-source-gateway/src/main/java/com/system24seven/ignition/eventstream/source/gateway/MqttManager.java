package com.system24seven.ignition.eventstream.source.gateway;

import static com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.system24seven.ignition.eventstream.source.MqttSourceConfig;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Dictionary;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONException;

public class MqttManager {
    private final LoggerEx logger;
    private final MqttSourceConfig settingsRecord;
    private Mqtt5AsyncClient client;
    private final AtomicReference<EventStreamSource.Subscriber> subscriber = new AtomicReference<>();

    /**
     * Represents a manager for handling MQTT operations.
     *
     * @param settingsRecord - The MQTT settings record containing broker configuration details
     */
    public MqttManager(MqttSourceConfig settingsRecord, EventStreamSource.Subscriber subscriber){
        this.logger = MqttSourceGatewayHook.getLogger();
        this.settingsRecord = settingsRecord;
        this.subscriber.set(subscriber);
    }

    /**
     * This method retrieves an MQTT 5 async client based on the configured settings. If TLS is enabled, it creates the client with SSL configuration, otherwise, it creates the client
     *  without SSL.
     *
     * @return Mqtt5AsyncClient - the MQTT 5 async client instance
     */
    public Mqtt5AsyncClient getMqttClient() {
        try {
          if (settingsRecord.getMQTlsEnable()) {
            client = MqttClient.builder()
                    .identifier("ignition" + "-" + UUID.randomUUID())
                    .serverHost(settingsRecord.getMQHostname())
                    .serverPort(settingsRecord.getMQHostPort())
                    .sslWithDefaultConfig()
                    .useMqttVersion5()
                    .executorConfig()
                    .nettyThreads(1)
                    .applyExecutorConfig()
                    .automaticReconnectWithDefaultConfig()
                    .buildAsync();
          } else {
            client = MqttClient.builder()
                    .identifier("ignition" + "-" + UUID.randomUUID())
                    .serverHost(settingsRecord.getMQHostname())
                    .serverPort(settingsRecord.getMQHostPort())
                    .useMqttVersion5()
                    .executorConfig()
                    .nettyThreads(1)
                    .applyExecutorConfig()
                    .automaticReconnectWithDefaultConfig()
                    .buildAsync();
          }
            return client;
        } catch (Exception e) {
      logger.fatal("Error starting up broker connection.", e);
      return null;
    }
  }

    /**
     * Subscribes to the specified root topic and connects to the provided MQTT client asynchronously.
     *
     * @param client the MQTT 5 async client to subscribe and connect to
     */
  public void subscribeAndConnect(Mqtt5AsyncClient client) {
        try {
            client.subscribeWith()
                    .topicFilter(settingsRecord.getMQTopicName())
                    .qos(AT_LEAST_ONCE)
                    .callback(mqtt5Publish -> {
                        try {
                            onMessage(mqtt5Publish);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .send()
                    .whenComplete((subAck, throwable) -> logger.trace("Subscribed: " + subAck + ", throwable: " + throwable));

            client.connectWith()
                .noSessionExpiry()
                .simpleAuth()
                .username(settingsRecord.getMQUsername())
                .password(settingsRecord.getMQPassword().getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((mqtt5ConnAck, throwable) -> logger.debug("Connected: " + mqtt5ConnAck + ", throwable: " + throwable));
        } catch (Exception e) {
            logger.fatal("Error starting up broker connection.", e);
        }
  }

    /**
     * Processes the received MQTT message.
     *
     * @param mqtt5Publish the Mqtt5Publish message received
     * @throws UnsupportedEncodingException if character encoding is not supported
     * @throws JSONException if there is an issue with JSON parsing
     */
   private void onMessage(final Mqtt5Publish mqtt5Publish) throws UnsupportedEncodingException, JSONException {
        logger.trace("Received message: " + mqtt5Publish);
        String payload = new String(mqtt5Publish.getPayloadAsBytes(), StandardCharsets.UTF_8); //Grab message payload as string
        Dictionary<String, String> params = new java.util.Hashtable<>();
        params.put("topic",removeLastChar(mqtt5Publish.getTopic().toString()));
        params.put("payload", payload);
        params.put("timestamp", Date.from(Instant.now()).toString());
        this.subscriber.get().submitEvent(EventPayload.builder(params).build());
    }

    private String removeLastChar(String str) {
        if (str != null && !str.isEmpty() && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * Publish message to defined topic
     * @param topic String containing topic path
     * @param payload String containing payload value to be published
     * @param qos QoS level of publish message
     */
    public void publishMessage(String topic, String payload,Enum<MqttQos> qos) {
        CompletableFuture<Mqtt5PublishResult> result = client.publishWith()
                .topic("test/topic")
                .qos(AT_LEAST_ONCE)
                .payload("payload".getBytes())
                .send()
                .whenComplete((mqtt5PublishResult, throwable) -> logger.trace("Message Sent: " + mqtt5PublishResult));
    }

    /**
     * Disconnects the MQTT Client
     */
    public void disconnect() {
        this.client.disconnect();
    }

    public void shutdown(){
    }
}
