package org.joget;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.lib.Radio;
import org.joget.apps.form.model.FormBuilderPalette;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSocket;
import org.joget.workflow.model.service.WorkflowUserManager;

public class WebSocketPlugin extends Radio implements PluginWebSocket {

    private final static String MESSAGE_PATH = "message/form/WebSocketPlugin";

    @Override
    public String getName() {
        return "WebSocketPlugin";
    }

    @Override
    public String getVersion() {
        return "5.0.0";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getLabel() {
        //support i18n
        return AppPluginUtil.getMessage("org.joget.WebSocketPlugin.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getDescription() {
        //support i18n
        return AppPluginUtil.getMessage("org.joget.WebSocketPlugin.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/" + getName() + ".json", null, true, MESSAGE_PATH);

    }

    @Override
    public FormRowSet formatData(FormData formData) {

        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (value != null) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }
        return rowSet;
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "webSocketPlugin.ftl";

        WorkflowUserManager wum = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        String username = wum.getCurrentUsername();
        dataModel.put("username", username);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    @Override
    public String getFormBuilderTemplate() {
        return "<label class='label'>" + getLabel() + "</label>";
    }

    @Override
    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_CUSTOM;
    }

    @Override
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText("Connection established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText("Server received: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(Session session) {
        LogUtil.info(getClassName(), "Webscoket connection closed");
    }
    
    @Override
    public void onError(Session session, Throwable throwable) {
        LogUtil.error(getClassName(), throwable, "");
    }

    public boolean isEnabled() {
        if ("true".equalsIgnoreCase(getPropertyString("enableWebsocket"))) {
            return true;
        } else {
            return false;
        }
    }

}
