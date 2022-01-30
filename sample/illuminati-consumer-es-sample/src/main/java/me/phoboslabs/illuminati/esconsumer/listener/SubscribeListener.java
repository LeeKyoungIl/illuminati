package me.phoboslabs.illuminati.esconsumer.listener;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient;
import me.phoboslabs.illuminati.esconsumer.config.model.SampleEsTemplateInterfaceModelImpl;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@EnableBinding(Sink.class)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscribeListener {

    private static final Logger SUB_LOGGER = LoggerFactory.getLogger(SubscribeListener.class);

    private final @NotNull EsClient esClient;

    @Value("${elasticsearchInfo.master.user:}")
    private String esUserName;
    @Value("${elasticsearchInfo.master.pass:}")
    private String esUserPass;

    @StreamListener(Sink.INPUT)
    public void subscribe (Message<?> message) {
        SampleEsTemplateInterfaceModelImpl sampleBuyEsModel = null;

        try {
            String jsonString = (String) message.getPayload();
            sampleBuyEsModel = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(jsonString, SampleEsTemplateInterfaceModelImpl.class);
        } catch (Exception ex) {
            SUB_LOGGER.error("Sorry. something is wrong in Parsing received dto. ({})", ex.getMessage(), ex);
            return;
        }

        if (sampleBuyEsModel != null) {
            if (StringObjectUtils.isValid(esUserName) && StringObjectUtils.isValid(esUserPass)) {
                sampleBuyEsModel.setEsUserAuth(this.esUserName, this.esUserPass);
            }

            sampleBuyEsModel.customData();
            try {
                HttpResponse result = (HttpResponse) this.esClient.save(sampleBuyEsModel);

                if ("2".equals(String.valueOf(result.getStatusLine().getStatusCode()).substring(0, 1))) {
                    SUB_LOGGER.info("successfully transferred dto to Elasticsearch.");
                } else {
                    SUB_LOGGER.error("Sorry. something is wrong in send to Elasticsearch. (code : "+result.getStatusLine().getStatusCode()+")");
                }
            } catch (Exception ex) {
                SUB_LOGGER.error("Sorry. something is wrong in send to Elasticsearch. (code : {})", ex.getMessage(), ex);
            }
        }
    }
}
