package me.phoboslabs.illuminati.elasticsearch.infra.param.source;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class EsSource {

    @Expose
    private final List<String> source = new ArrayList<String>();

    EsSource () {

    }

    public void setSource (String columnName) {
        if (this.source.contains(columnName) == false) {
            this.source.add(columnName);
        }
    }

    public List<String> getSource () {
        return this.source;
    }
}
