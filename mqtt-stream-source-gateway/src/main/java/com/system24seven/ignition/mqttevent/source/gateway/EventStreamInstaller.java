package com.system24seven.ignition.mqttevent.source.gateway;

import com.inductiveautomation.eventstream.gateway.EventStreamManager;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 * This class holds code that is specific to the event stream module. This is to prevent the GatewayHook from crashing
 * on a {@link ClassNotFoundException} when the event stream module is not installed. This class is only loaded if the
 * event stream module is installed via the following check:
 * */
public class EventStreamInstaller {

    public static void setup(GatewayContext context) {
        EventStreamManager.get(context).getSourceRegistry().register(
            MqttSource.createFactory()
        );
    }

}
