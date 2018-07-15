package me.phoboslabs.illuminati.common.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class IlluminatiStringBuilder {

    private final List<String> bufStringData = new ArrayList<String>();

    public IlluminatiStringBuilder () {

    }

    public void appendString (String stringData) {
        this.bufStringData.add(stringData);
    }

    public String toStringWithDelimiter (final String delimeter) {
        if (CollectionUtils.isEmpty(this.bufStringData)) {
            return "";
        } else {
            StringBuilder returnStringData = new StringBuilder();

            for (int i=0; i<this.bufStringData.size(); i++) {
                if (i > 0) {
                    returnStringData.append(delimeter);
                }
                returnStringData.append(this.bufStringData.get(i));
            }

            return returnStringData.toString();
        }


    }
}
