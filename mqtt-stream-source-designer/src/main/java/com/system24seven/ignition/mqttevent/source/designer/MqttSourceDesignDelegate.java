package com.system24seven.ignition.mqttevent.source.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.EventStreamSourceDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.system24seven.ignition.mqttevent.source.MqttSourceModule;

public class MqttSourceDesignDelegate implements EventStreamSourceDesignDelegate {

    @Override
    public SourceEditor getEditor(EventStreamContext context) {
        return new MqttSourceEditor();
    }

    @Override
    public String getType() {
        return MqttSourceModule.MODULE_ID;
    }

}
