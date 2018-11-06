package org.activiti.engine.impl.bpmn.helper;

import org.activiti.bpmn.model.IOParameter;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

public class TaskVariableCopier {

    public static void copyVariablesIntoTaskLocal(TaskEntity task, CommandContext context, List<IOParameter> inParameters){

        //TODO: would like to filter which variables copy much as with subProcesses

        HashMap<String,Object> variables = new HashMap<>();
        for (IOParameter ioParameter : inParameters) {
            Object value = null;
            if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
                Expression expression = context.getProcessEngineConfiguration().getExpressionManager().createExpression(ioParameter.getSourceExpression().trim());
                value = expression.getValue(execution);

            } else {
                value = execution.getVariable(ioParameter.getSource());
            }
            variables.put(ioParameter.getTarget(), value);
        }

        if(variables.isEmpty()) {
            task.setVariablesLocal(task.getVariables());
        }


    }

    public static void copyVariablesOutFromTaskLocal(TaskEntity task){

        //TODO: would like to filter which variables copy much as with subProcesses

        for (IOParameter ioParameter : callActivity.getOutParameters()) {
            Object value = null;
            if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(ioParameter.getSourceExpression().trim());
                value = expression.getValue(subProcessInstance);

            } else {
                value = subProcessInstance.getVariable(ioParameter.getSource());
            }
            execution.setVariable(ioParameter.getTarget(), value);
        }

        //provided not a standalone task
        if(task.getProcessInstance()!=null) {
            task.getProcessInstance().setVariables(task.getVariablesLocal());
        }
    }
}
