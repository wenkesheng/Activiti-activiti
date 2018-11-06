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
package org.activiti.bpmn.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.apache.commons.lang3.StringUtils;

/**

 */
public class CallActivityXMLConverter extends BaseBpmnXMLConverter {

  protected Map<String, BaseChildElementParser> childParserMap = new HashMap<String, BaseChildElementParser>();

  public CallActivityXMLConverter() {
    InParameterParser inParameterParser = new InParameterParser();
    childParserMap.put(inParameterParser.getElementName(), inParameterParser);
    OutParameterParser outParameterParser = new OutParameterParser();
    childParserMap.put(outParameterParser.getElementName(), outParameterParser);
  }

  public Class<? extends BaseElement> getBpmnElementType() {
    return CallActivity.class;
  }

  @Override
  protected String getXMLElementName() {
    return ELEMENT_CALL_ACTIVITY;
  }

  @Override
  protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
    CallActivity callActivity = new CallActivity();
    BpmnXMLUtil.addXMLLocation(callActivity, xtr);
    callActivity.setCalledElement(xtr.getAttributeValue(null, ATTRIBUTE_CALL_ACTIVITY_CALLEDELEMENT));
    callActivity.setBusinessKey(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_CALL_ACTIVITY_BUSINESS_KEY));
    callActivity.setInheritBusinessKey(Boolean.parseBoolean(xtr.getAttributeValue(
        ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_CALL_ACTIVITY_INHERIT_BUSINESS_KEY)));
    callActivity.setInheritVariables(Boolean.valueOf(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_CALL_ACTIVITY_INHERITVARIABLES)));
    parseChildElements(getXMLElementName(), callActivity, childParserMap, model, xtr);
    return callActivity;
  }

  @Override
  protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
    CallActivity callActivity = (CallActivity) element;
    if (StringUtils.isNotEmpty(callActivity.getCalledElement())) {
      xtw.writeAttribute(ATTRIBUTE_CALL_ACTIVITY_CALLEDELEMENT, callActivity.getCalledElement());
    }
    if (StringUtils.isNotEmpty(callActivity.getBusinessKey())) {
      writeQualifiedAttribute(ATTRIBUTE_CALL_ACTIVITY_BUSINESS_KEY, callActivity.getBusinessKey(), xtw);
    }
    if (callActivity.isInheritBusinessKey()) {
      writeQualifiedAttribute(ATTRIBUTE_CALL_ACTIVITY_INHERIT_BUSINESS_KEY, "true", xtw);
    }
    xtw.writeAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_CALL_ACTIVITY_INHERITVARIABLES, String.valueOf(callActivity.isInheritVariables()));
  }

  @Override
  protected boolean writeExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
    CallActivity callActivity = (CallActivity) element;
    didWriteExtensionStartElement = writeIOParameters(ELEMENT_CALL_ACTIVITY_IN_PARAMETERS,
        callActivity.getInParameters(), didWriteExtensionStartElement, xtw);
    didWriteExtensionStartElement = writeIOParameters(ELEMENT_CALL_ACTIVITY_OUT_PARAMETERS,
        callActivity.getOutParameters(), didWriteExtensionStartElement, xtw);
    return didWriteExtensionStartElement;
  }

  @Override
  protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
  }

}
