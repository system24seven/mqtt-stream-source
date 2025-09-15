package com.system24seven.ignition.mqttevent.source.designer;

import com.inductiveautomation.eventstream.designer.EventStreamDesignerHook;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.system24seven.ignition.mqttevent.source.MqttSourceModule;

public class MqttSourceDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        // checks if the event stream module is installed
        if (context.getModule(MqttSourceModule.EVENT_STREAM_MODULE_ID) != null) {
            var hook = EventStreamDesignerHook.get(context);
            if (hook != null) {
                hook.getEventStreamManager().getSourceRegistry().register(
                    new MqttSourceDesignDelegate()
                );
            }
        }
    }
}
