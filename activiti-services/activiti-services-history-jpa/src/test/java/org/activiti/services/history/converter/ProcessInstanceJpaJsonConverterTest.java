/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.services.history.converter;

import org.activiti.services.model.ProcessInstance;
import org.activiti.services.model.ProcessInstanceStatus;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.activiti.test.Assertions.assertThat;

public class ProcessInstanceJpaJsonConverterTest {

    private ProcessInstanceJpaJsonConverter converter = new ProcessInstanceJpaJsonConverter();

    @Test
    public void convertToDatabaseColumnShouldReturnTheEntityJsonRepresentation() throws Exception {
        //given
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setName("My instance");
        processInstance.setStatus(ProcessInstanceStatus.ACTIVE);
        processInstance.setProcessDefinitionId("proc-def-id");
        processInstance.setProcessDefinitionKey("proc-def-key");
        processInstance.setId("20");

        //when
        String jsonRepresentation = converter.convertToDatabaseColumn(processInstance);

        //then
        assertThatJson(jsonRepresentation)
                .node("name").isEqualTo("My instance")
                .node("status").isEqualTo("ACTIVE")
                .node("processDefinitionId").isEqualTo("proc-def-id")
                .node("processDefinitionKey").isEqualTo("proc-def-key")
                .node("id").isEqualTo("\"20\"");
    }

    @Test
    public void convertToEntityAttributeShouldCreateAProcessInstanceWithFieldsSet() throws Exception {
        //given
        String jsonRepresentation =
                "{\"id\":\"20\"," +
                "\"status\":\"ACTIVE\"," +
                "\"name\":\"My instance\"," +
                "\"processDefinitionId\":\"proc-def-id\"," +
                "\"processDefinitionKey\":\"proc-def-key\"}";

        //when
        ProcessInstance processInstance = converter.convertToEntityAttribute(jsonRepresentation);

        //then
        assertThat(processInstance)
                .isNotNull()
                .hasId("20")
                .hasStatus(ProcessInstanceStatus.ACTIVE)
                .hasProcessDefinitionId("proc-def-id")
                .hasProcessDefinitionKey("proc-def-key");
    }

}