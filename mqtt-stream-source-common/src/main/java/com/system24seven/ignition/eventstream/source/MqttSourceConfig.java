package com.system24seven.ignition.eventstream.source;

import com.inductiveautomation.ignition.common.gson.JsonObject;

import java.util.Objects;

public record MqttSourceConfig(String mqHostname,
                               String mqTlsEnable,
                               String mqHostPort,
                               String mqUsername,
                               String mqPassword,
                               String mqTopic) {

    public static final String MQHostname = "brokerHostname";
    public static final String MQTlsEnable = "enableTls";
    public static final String MQHostPort = "brokerPort";
    public static final String MQUsername = "brokerUsername";
    public static final String MQPassword = "brokerPassword";
    public static final String MQTopic = "topicName";


    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty(MQHostname, mqHostname);
        json.addProperty(MQTlsEnable, mqTlsEnable);
        json.addProperty(MQHostPort, mqHostPort);
        json.addProperty(MQUsername, mqUsername);
        json.addProperty(MQPassword, mqPassword);
        json.addProperty(MQTopic,mqTopic);
        return json;
    }

    public static MqttSourceConfig fromJson(JsonObject config) {
        if (config == null || config.isEmpty()) {
            return defaultConfig();
        }
        return new MqttSourceConfig(
                config.get(MQHostname).getAsString(),
                config.get(MQTlsEnable).getAsString(),
                config.get(MQHostPort).getAsString(),
                config.get(MQUsername).getAsString(),
                config.get(MQPassword).getAsString(),
                config.get(MQTopic).getAsString());
    }

    public static MqttSourceConfig defaultConfig() {
        return new MqttSourceConfig("broker.hivemq.com","false","1883","","","bre/");
    }

    public String getMQHostname() {
        return mqHostname;
    }

    public Boolean getMQTlsEnable() {
        return Objects.equals(mqTlsEnable, "true");
    }

    public Integer getMQHostPort() {
        return Integer.valueOf(mqHostPort.replaceAll("\\p{Punct}", ""));
    }

    public String getMQUsername() {
        return mqUsername;
    }
    public String getMQPassword() {
        return mqPassword;
    }
    public String getMQTopicName() {
        return mqTopic;
    }
}
