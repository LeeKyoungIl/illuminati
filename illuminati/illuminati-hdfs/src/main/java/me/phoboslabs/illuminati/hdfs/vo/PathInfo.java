package me.phoboslabs.illuminati.hdfs.vo;

import org.apache.hadoop.fs.Path;

public class PathInfo {

    private final Path path;
    private final boolean isExists;

    public PathInfo(final Path path, final boolean isExists) {
        this.path = path;
        this.isExists = isExists;
    }

    public boolean isExists() {
        return this.isExists;
    }

    public boolean isNotExists() {
        return this.isExists == false;
    }

    public Path getPath() {
        return this.path;
    }
}
