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
import java.util.stream.Collectors;

public class SimpleInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger SIMPLE_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(SimpleInfraTemplateImpl.class);

    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";


    public SimpleInfraTemplateImpl(final String propertiesName) throws Exception {
        super(propertiesName);

        this.checkRequiredValuesForInit();
        this.initProperties();
    }

    @Override
    public void sendToIlluminati(String entity) throws PublishMessageException, Exception {
        IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(entity, IlluminatiTemplateInterfaceModelImpl.class);
        final RequestGeneralModel requestGeneralModel = illuminatiTemplateInterfaceModel.getGeneralRequestMethodInfo();
        final String fullMethodInfo = requestGeneralModel.getMethodName();
        final Map<String, Object> methodInfo = this.getRequestMethod(fullMethodInfo);
        final Map<String, Object> fullMethodParam = requestGeneralModel.getMethodParams();
        final long elapsedTime = illuminatiTemplateInterfaceModel.getElapsedTime();
        final String logTime = illuminatiTemplateInterfaceModel.getLogTime();
        final Map<String, Object> resultOutput = illuminatiTemplateInterfaceModel.getOutput();

        SIMPLE_TEMPLATE_IMPL_LOGGER.info("");
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ClassName: {} {} {}", TEXT_RED, methodInfo.get("className"), TEXT_RESET);
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ MethodName: {}", methodInfo.get("methodName"));
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ┗ ReturnType: {}", methodInfo.get("returnType"));

        List<String> paramsType = (List) methodInfo.get("paramsType");
        int index = 0;
        for (String key :  fullMethodParam.keySet()) {
            SIMPLE_TEMPLATE_IMPL_LOGGER.info("@ i-sm @ ┗ params -> type: {}, name: {}, value: {}", paramsType.get(index), key, fullMethodParam.get(key));
            index += 1;
        }
        SIMPLE_TEMPLATE_IMPL_LOGGER.info("");

    }

    private Map<String, Object> getRequestMethod(final String fullMethodInfo) {
        try {
            Map<String, Object> requestInfoMap = new HashMap<>();

            final String targetRequestInfo = fullMethodInfo.replace("public ", "");
            final String[] returnType = targetRequestInfo.split("\\s");

            requestInfoMap.put("returnType", returnType[0]);

            final String[] classMethodInfo = returnType[1].split("\\(");
            final String[] classMethod = classMethodInfo[0].split("\\.");
            final String className = classMethod[classMethod.length-2];
            final String methodName = classMethod[classMethod.length-1];

            requestInfoMap.put("className", className);
            requestInfoMap.put("methodName", methodName);

            final String[] paramInfo = classMethodInfo[1].replace(")", "").split(",");
            List<String> params = Arrays.stream(paramInfo)
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
