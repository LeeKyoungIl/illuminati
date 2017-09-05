package com.leekyoungil.illuminati.esconsumer.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellin.me on 10/08/2017.
 */
@Data
@NoArgsConstructor
public class ElasticsearchInfo {

    private String host;
    private int port;
}
