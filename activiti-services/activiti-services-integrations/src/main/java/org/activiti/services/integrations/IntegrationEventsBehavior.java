package org.activiti.services.integrations;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.services.events.ProcessEngineChannels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class IntegrationEventsBehavior extends TaskActivityBehavior {

    @Value("${spring.application.name}")
    protected String applicationName;

    @Autowired
    private ProcessEngineChannels producer;

    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> transientVariables = execution.getTransientVariables();
        Map<String, VariableInstance> variableInstances = execution.getVariableInstances();
        Map<String, Object> variables = execution.getVariables();

        String currentActivityId = execution.getCurrentActivityId();

        producer.integrationProducer().send(MessageBuilder.withPayload(newEvent).build());
    }
}
