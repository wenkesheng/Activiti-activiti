/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.spring.process;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.spring.process.model.Extension;
import org.activiti.spring.process.model.ProcessExtensionModel;
import org.activiti.spring.process.model.VariableDefinition;
import org.activiti.spring.process.variable.types.ExtensionVariableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProcessExtensionService {

    private String processExtensionsRoot;
    private String processExtensionsSuffix;
    private final ObjectMapper objectMapper;
    private ResourcePatternResolver resourceLoader;
    private Map<String, ExtensionVariableType> variableTypeMap;

    public ProcessExtensionService(String processExtensionsRoot, String processExtensionsSuffix,
                                   ObjectMapper objectMapper, ResourcePatternResolver resourceLoader,
                                   Map<String, ExtensionVariableType> variableTypeMap) {
        this.processExtensionsRoot = processExtensionsRoot;
        this.processExtensionsSuffix = processExtensionsSuffix;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.variableTypeMap = variableTypeMap;
    }

    private Optional<Resource[]> retrieveResources() throws IOException {
        Optional<Resource[]> resources = Optional.empty();
        Resource processExtensionsResource = resourceLoader.getResource(processExtensionsRoot);
        if (processExtensionsResource.exists()) {
            return Optional.ofNullable(resourceLoader.getResources(processExtensionsRoot + processExtensionsSuffix));
        }
        return resources;
    }

    private ProcessExtensionModel read(InputStream inputStream) throws IOException {
        ProcessExtensionModel mappedModel = objectMapper.readValue(inputStream,
                ProcessExtensionModel.class);
        return convertJsonVariables(mappedModel);
    }

    /**
     * Json variables need to be represented as JsonNode for engine to handle as Json
     * Do this for any var marked as json or whose type is not recognised from the extension file
     */
    private ProcessExtensionModel convertJsonVariables(ProcessExtensionModel processExtensionModel){
        if( processExtensionModel!=null && processExtensionModel.getExtensions()!=null
                && processExtensionModel.getExtensions().getProperties()!=null ){

            for(VariableDefinition variableDefinition:processExtensionModel.getExtensions().getProperties().values()){
                if(!variableTypeMap.keySet().contains(variableDefinition.getType())||variableDefinition.getType().equals("json")){
                    variableDefinition.setValue(objectMapper.convertValue(variableDefinition.getValue(), JsonNode.class));
                }
            }
        }
        return processExtensionModel;
    }

    public Map<String, ProcessExtensionModel> get() throws IOException {
        List<ProcessExtensionModel> processExtensionModels = new ArrayList<>();
        Optional<Resource[]> resourcesOptional = retrieveResources();
        if (resourcesOptional.isPresent()) {
            for (Resource resource : resourcesOptional.get()) {
                processExtensionModels.add(read(resource.getInputStream()));
            }
        }
        return convertToMap(processExtensionModels);
    }

    private Map<String, ProcessExtensionModel> convertToMap(List<ProcessExtensionModel> processExtensionModelList){
        return processExtensionModelList.stream()
                .collect(Collectors.toMap(ProcessExtensionModel::getId,
                        Function.identity()));
    }
}