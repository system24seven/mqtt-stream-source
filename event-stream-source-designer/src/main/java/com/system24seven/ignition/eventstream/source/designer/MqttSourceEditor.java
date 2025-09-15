package com.system24seven.ignition.eventstream.source.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.inductiveautomation.ignition.common.gson.JsonObject;

import javax.swing.*;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.system24seven.ignition.eventstream.source.MqttSourceConfig;
import net.miginfocom.swing.MigLayout;
import org.slf4j.ILoggerFactory;

import java.util.Objects;

public class MqttSourceEditor extends SourceEditor {

    private final JTextField mqHostname = new JTextField();
    private final JTextField mqHostPort = new JTextField();
    private final JCheckBox mqTlsEnable = new JCheckBox();
    private final JTextField mqUsername = new JTextField();
    private final JPasswordField mqPassword = new JPasswordField();
    private final JTextField mqTopic = new JTextField();
    private static final LoggerEx logger = LoggerEx.newBuilder().build("mqtester");

    public MqttSourceEditor() {
        super();
        setLayout(new MigLayout(
            "ins 0, fillx, gapy 4, wrap 1",
            "[fill, grow]", "")
        );
        add(new JLabel("MQTT Broker IP:"));
        add(mqHostname, "width 20:400:400, wrap 16");
        add(new JLabel("MQTT Broker Port:"));
        add(mqHostPort, "width 20:400:400, wrap 16");
        add(new JLabel("Use TLS:"));
        add(mqTlsEnable, "width 20:400:400, wrap 16");
        add(new JLabel("MQTT Broker Username:"));
        add(mqUsername, "width 20:400:400, wrap 16");
        add(new JLabel("MQTT Broker Password:"));
        add(mqPassword, "width 20:400:400, wrap 16");
        add(new JLabel("MQTT Topic to subscribe to:"));
        add(mqTopic, "width 20:400:400, wrap 16");
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public void initialize(EventStreamContext context, JsonObject json) {
        MqttSourceConfig config = MqttSourceConfig.fromJson(json);
        mqHostname.setText(config.mqHostname());
        mqTlsEnable.setSelected(Objects.equals(config.mqTlsEnable(), "true"));
        mqHostPort.setText(String.valueOf(config.mqHostPort()));
        mqUsername.setText(config.mqUsername());
        mqPassword.setText(config.mqPassword());
        logger.infof("mqPassword: %s", config.mqPassword());
        mqTopic.setText(config.getMQTopicName());
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public JsonObject getConfig() {
        new MqttSourceConfig("","","","","","");
        return new MqttSourceConfig(
                mqHostname.getText(),
                mqTlsEnable.isSelected()?"true":"false",
                mqHostPort.getText(),
                mqUsername.getText(),
                new String(mqPassword.getPassword()),
                mqTopic.getText()).toJson();
    }
}

