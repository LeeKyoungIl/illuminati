package me.phoboslabs.illuminati.processor.infra.simple.impl;

import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.BasicTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger SIMPLE_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(SimpleInfraTemplateImpl.class);

    public SimpleInfraTemplateImpl(final String propertiesName) throws Exception {
        super(propertiesName);

        this.checkRequiredValuesForInit();
        this.initProperties();
    }

    @Override
    public void sendToIlluminati(String entity) throws PublishMessageException, Exception {

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
