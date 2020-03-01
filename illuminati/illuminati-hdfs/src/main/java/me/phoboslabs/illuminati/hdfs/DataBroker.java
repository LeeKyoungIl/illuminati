package me.phoboslabs.illuminati.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public interface DataBroker {

    boolean addFile(final String source, final String dest, final boolean overwrite, final boolean withNewLine);

    String readFile(final String source) throws Exception;

    boolean deleteFile(final String source, boolean forceDelete);

    boolean mkdir(final String source);

    Configuration getConfiguration();
}
