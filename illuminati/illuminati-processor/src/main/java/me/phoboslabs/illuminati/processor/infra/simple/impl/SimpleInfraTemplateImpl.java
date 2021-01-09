/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.infra.simple.impl;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.RequestGeneralModel;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.BasicTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimpleInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger SIMPLE_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(SimpleInfraTemplateImpl.class);

    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_BR_GREEN = "\033[42;1m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_BR_BLUE = "\033[44;1m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_BR_PURPLE = "\033[45;1m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_BR_CYAN = "\033[46;1m";
    public static final String TEXT_BR_RED = "\033[41m";
    public static final String TEXT_WHITE = "\u001B[37m";
    public static final String TEXT_BR_WHITE = "\033[47;1m";

    private final static String OUTPUT_RESULT_STRING_KEY_NAME = "resultString";
    private final static String OUTPUT_RESULT_OBJECT_KEY_NAME = "resultObject";

    public SimpleInfraTemplateImpl(final String propertiesName) throws Exception {
        super(propertiesName);

        this.checkRequiredValuesForInit();
        this.initProperties();
    }

    @Override
    public void sendToIlluminati(String entity) throws PublishMessageException, Exception {
        final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(entity, IlluminatiTemplateInterfaceModelImpl.class);
        final RequestGeneralModel requestGeneralModel = illuminatiTemplateInterfaceModel.getGeneralRequestMethodInfo();
        final String fullMethodInfo = requestGeneralModel.getMethodName();
        final Map<String, Object> methodInfo = this.getRequestMethod(fullMethodInfo);
        final Map<String, Object> fullMethodParam = requestGeneralModel.getMethodParams();
        final long methodExecutionTime = illuminatiTemplateInterfaceModel.getElapsedTime();
        final String logTime = illuminatiTemplateInterfaceModel.getLogTime();
        final Map<String, Object> resultOutput = illuminatiTemplateInterfaceModel.getOutput();

        try {
            this.printSimpleLogMessages(logTime, methodInfo, methodExecutionTime, fullMethodParam, resultOutput);
        } catch (Exception ex) {
            SIMPLE_TEMPLATE_IMPL_LOGGER.error("simple trace model parsing exception", ex);
        }
    }

    private void printSimpleLogMessages(final String logTime, final Map<String, Object> methodInfo, final long methodExecutionTime, final Map<String, Object> fullMethodParam, final Map<String, Object> resultOutput) {
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("");
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ Logging time: {} {} {}", TEXT_BR_WHITE, logTime, TEXT_RESET);
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ Class name: {} {} {}", TEXT_RED, methodInfo.get("className"), TEXT_RESET);
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ Method name: {} {} {}, method execution time: {} {}ms {}", TEXT_GREEN, methodInfo.get("methodName"), TEXT_RESET, TEXT_BR_GREEN, methodExecutionTime, TEXT_RESET);

        List<String> paramsType = (List) methodInfo.get("paramsType");

        final AtomicInteger index = new AtomicInteger();
        fullMethodParam.entrySet().stream().forEach(entry -> SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ┗ params -> type: {}{} {},  name: {}{}, {} value: {}{} {}", TEXT_YELLOW, paramsType.get(index.getAndIncrement()), TEXT_RESET
                ,  TEXT_BLUE, entry.getKey(), TEXT_RESET, TEXT_BR_RED, fullMethodParam.get(entry.getKey()), TEXT_RESET));

        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ Return type: {} {} {}", TEXT_CYAN, methodInfo.get("returnType"), TEXT_RESET);

        resultOutput.entrySet().stream().forEach(entry -> {
            if (OUTPUT_RESULT_OBJECT_KEY_NAME.equalsIgnoreCase(entry.getKey())) {
                Map<String, Object> resultValueMap = ((Map<String, Object>)resultOutput.get(OUTPUT_RESULT_OBJECT_KEY_NAME));
                resultValueMap.entrySet().stream()
                        .forEach(resultEntry -> SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ┗ name: {}{}, {} value: {}{} {}", TEXT_BLUE, resultEntry.getKey(), TEXT_RESET, TEXT_BR_RED, resultValueMap.get(resultEntry.getKey()), TEXT_RESET));
            } else if (OUTPUT_RESULT_STRING_KEY_NAME.equalsIgnoreCase(entry.getKey())) {
                Map<String, Object> resultValueMap = ((Map<String, Object>)resultOutput.get(OUTPUT_RESULT_STRING_KEY_NAME));
                resultValueMap.entrySet().stream().forEach(resultEntry -> SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ┗ value: {}{} {}", TEXT_BR_RED, resultValueMap.get(resultEntry.getKey()), TEXT_RESET));
            }
        });
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("");
    }

    private Map<String, Object> getRequestMethod(final String fullMethodInfo) {
        try {
            final String targetRequestInfo = fullMethodInfo.replace("public ", "");
            final String[] returnType = targetRequestInfo.split("\\s");
            final String[] classMethodInfo = returnType[1].split("\\(");
            final String[] classMethod = classMethodInfo[0].split("\\.");
            final String className = classMethod[classMethod.length-2];
            final String methodName = classMethod[classMethod.length-1];

            final Map<String, Object> requestInfoMap = new HashMap<>();
            requestInfoMap.put("returnType", returnType[0]);
            requestInfoMap.put("className", className);
            requestInfoMap.put("methodName", methodName);

            final String[] paramInfo = classMethodInfo[1].replace(")", "").split(",");
            final List<String> params = Arrays.stream(paramInfo)
                                        .map(param -> param.split("\\."))
                                        .map(paramClass -> paramClass[paramClass.length - 1])
                                        .collect(Collectors.toCollection(() -> new ArrayList<>(paramInfo.length)));

            if (CollectionUtils.isNotEmpty(params)) {
                requestInfoMap.put("paramsType", params);
            }

            return requestInfoMap;
        } catch (Exception ex) {
            SIMPLE_TEMPLATE_IMPL_LOGGER.error("request method info parsing error.");
            return null;
        }
    }

    @Override
    public boolean canIConnect() {
        return true;
    }

    @Override
    public void connectionClose() {

    }

    @Override
    public void validateBasicTemplateClass() throws ValidationException {

    }

    @Override
    protected void checkRequiredValuesForInit() {

    }

    @Override
    protected void initProperties() throws Exception {

    }
}
