package com.system24seven.ignition.mqttevent.source.gateway;

import static com.system24seven.ignition.mqttevent.source.MqttSourceModule.MODULE_ID;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.inductiveautomation.eventstream.SourceDescriptor;
import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.system24seven.ignition.mqttevent.source.MqttSourceConfig;
import com.system24seven.ignition.mqttevent.source.MqttSourceModule;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Starts a listener for SNMP Traps and converts them to events
 */
public class MqttSource implements EventStreamSource {

    public static Factory createFactory() {
        return new Factory() {
            @Override
            public SourceDescriptor getDescriptor() {
                return new SourceDescriptor(
                        MqttSourceModule.MODULE_ID,
                        MqttSourceModule.MODULE_NAME,
                    "Subscribes to an MQTT Topic and emits messages as events."
                );
            }

            @Override
            public EventStreamSource create(EventStreamContext context, JsonObject jsonConfig) {
                return new MqttSource(context, MqttSourceConfig.fromJson(jsonConfig));
            }
        };
    }

    private final EventStreamContext context;
    private final AtomicReference<Subscriber> subscriber = new AtomicReference<>();

    private MqttManager mqttManager;
    private final MqttSourceConfig config;
    private Mqtt5AsyncClient client;


    public MqttSource(EventStreamContext context, MqttSourceConfig config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public void onStartup(Subscriber subscriber) {
        context.logger().infof("Starting %s", MODULE_ID);
        context.logger().infof("Starting MqttClient");
        try {
            mqttManager = new MqttManager(config,subscriber);
        } catch (Exception e){
            context.logger().errorf("Error loading MQTT manager: " + e.getMessage(), e);
        }
        context.logger().infof("Starting MqttClient on %s:%s", config.getMQHostname(), config.getMQHostPort());
        client = mqttManager.getMqttClient();
        mqttManager.subscribeAndConnect(client);
        context.logger().infof("Started MqttClient on %s:%s", config.getMQHostname(), config.getMQHostPort());
    }

    @Override
    public void onShutdown() {
        context.logger().infof("Shutting down %s", MODULE_ID);
        client.disconnect();
        mqttManager.shutdown();
        subscriber.set(null);
    }
}