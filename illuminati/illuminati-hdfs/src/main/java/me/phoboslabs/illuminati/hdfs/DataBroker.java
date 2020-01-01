package me.phoboslabs.illuminati.hdfs;

public interface DataBroker {

    boolean addFile(final String source, final String dest, final boolean overwrite);

    String readFile(final String source) throws Exception;

    boolean deleteFile(final String source, boolean forceDelete);

    boolean mkdir(final String source);
}
