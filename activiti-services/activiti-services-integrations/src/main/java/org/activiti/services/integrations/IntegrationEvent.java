package org.activiti.services.integrations;

import java.util.Map;

public class IntegrationEvent {

    private String processDefinitionId;
    private String processInstanceId;
    private Map<String, Object> variables;
}
