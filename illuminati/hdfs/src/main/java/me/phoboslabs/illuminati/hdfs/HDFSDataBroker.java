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

package me.phoboslabs.illuminati.hdfs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import me.phoboslabs.illuminati.hdfs.vo.HDFSConnectionInfo;
import me.phoboslabs.illuminati.hdfs.vo.PathInfo;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/19/2019.
 * <p>
 * - It should be made of the Spring Bean or Singleton. example) * Singleton :
 * HDFSDataBroker.getInstance({HDFSConnectionInfo.class}); * Spring Bean : #@Bean public HDFSDataBroker
 * hdfsDataBroker(HDFSConnectionInfo hdfsConnectionInfo) { return new HDFSDataBroker(hdfsConnectionInfo); }
 */
public class HDFSDataBroker implements DataBroker {

    private final static Logger HDFS_PROCESSOR_LOGGER = LoggerFactory.getLogger(HDFSDataBroker.class);

    private static final class HDFSDataProcessorHolder {

        private static HDFSDataBroker INSTANCE_HOLDER;
        private static final Object LOCK_OBJ = new Object();

        private static HDFSDataBroker getInstance(HDFSConnectionInfo hdfsConnectionInfo) {
            if (INSTANCE_HOLDER == null) {
                synchronized (LOCK_OBJ) {
                    if (INSTANCE_HOLDER != null) {
                        return INSTANCE_HOLDER;
                    } else {
                        if (hdfsConnectionInfo == null) {
                            throw new IllegalArgumentException("hdfsConnectionInfo must not be null.");
                        }
                        INSTANCE_HOLDER = new HDFSDataBroker(hdfsConnectionInfo);
                    }
                }
            }

            return INSTANCE_HOLDER;
        }
    }

    private final Configuration configuration = new Configuration();

    public static HDFSDataBroker getInstance(HDFSConnectionInfo hdfsConnectionInfo) {
        return HDFSDataProcessorHolder.getInstance(hdfsConnectionInfo);
    }

    public HDFSDataBroker(HDFSConnectionInfo hdfsConnectionInfo) {
        this.init(hdfsConnectionInfo);
    }

    private final String uriKey = "fs.defaultFS";
    private final String hdfsImplKey = "fs.hdfs.impl";
    private final String fileImplKey = "fs.file.impl";
    private final String securityAuthenticationKey = "hadoop.security.authentication";
    private final String securityAuthorizationKey = "hadoop.security.authorization";
    private final String rpcTimeout = "fs.mapr.rpc.timeout";
    private final String dfsSupportAppendKey = "dfs.support.append";

    private final String userNameKey = "HADOOP_USER_NAME";
    private final String homeDirKey = "hadoop.home.dir";

    private void init(HDFSConnectionInfo hdfsConnectionInfo) {
        System.setProperty(this.userNameKey, hdfsConnectionInfo.getHDFSUser());
        System.setProperty(this.homeDirKey, hdfsConnectionInfo.getHomeDir());

        this.configuration.set(this.hdfsImplKey, org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        this.configuration.set(this.fileImplKey, org.apache.hadoop.fs.LocalFileSystem.class.getName());
        this.configuration.set(this.uriKey, hdfsConnectionInfo.getHDFSUriAddress());
        this.configuration.set(this.securityAuthenticationKey, hdfsConnectionInfo.getHDFSSecurityAuthenticationType());
        this.configuration.set(this.securityAuthorizationKey, hdfsConnectionInfo.getHDFSSecurityAuthorizationValue());
        this.configuration.set(this.rpcTimeout, hdfsConnectionInfo.getRpcTimeout());
        this.configuration.set(this.dfsSupportAppendKey, hdfsConnectionInfo.isDfsSupportAppend());
    }

    @Override
    public boolean addFile(String source, String dest, boolean overwrite, boolean withNewLine) {
        try (FileSystem fileSystem = FileSystem.get(URI.create(this.configuration.get(this.uriKey)), this.configuration)) {
            PathInfo pathInfo = this.checkPathAndGet(dest, fileSystem);
            Path path = pathInfo.getPath();
            FSDataOutputStream out;
            if (!pathInfo.isExists()) {
                out = fileSystem.create(path);
            } else {
                if (overwrite) {
                    fileSystem.delete(path, true);
                    out = fileSystem.create(path);
                } else {
                    out = fileSystem.append(path);
                    if (withNewLine) {
                        out.writeBytes(System.lineSeparator());
                    }
                }
            }

            File sourceFile = new File(source);
            try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
                return this.writeFileSystem(fileInputStream, out);
            } catch (Exception ex) {
                HDFS_PROCESSOR_LOGGER.error("An error occurred checking of file input stream. ({})", ex.toString());
            } finally {
                out.close();
            }
        } catch (Exception ex) {
            HDFS_PROCESSOR_LOGGER.error("An error occurred checking of file system. ({})", ex.toString());
        }
        return false;
    }

    private final int bytePerOnce = 1024;

    private boolean writeFileSystem(FileInputStream fileInputStream, FSDataOutputStream out) {
        try (InputStream inputStream = new BufferedInputStream(fileInputStream)) {
            byte[] byteData = new byte[this.bytePerOnce];
            int numBytes = 0;
            while ((numBytes = inputStream.read(byteData)) > 0) {
                out.write(byteData, 0, numBytes);
            }
            final int wroteSize = out.size();
            return wroteSize > 0;
        } catch (Exception ex) {
            HDFS_PROCESSOR_LOGGER.error("An error occurred writing to file system. ({})", ex.toString());
        }
        return false;
    }

    @Override
    public String readFile(String source) throws Exception {
        try (FileSystem fileSystem = FileSystem.get(URI.create(this.configuration.get(this.uriKey)), this.configuration)) {
            PathInfo pathInfo = this.checkPathAndGet(source, fileSystem);
            if (pathInfo.isNotExists()) {
                throw new Exception("File is not exists. check this(" + source + ") location.");
            }
            return this.readFileSystem(fileSystem, pathInfo.getPath());
        } catch (Exception ex) {
            final String errorMessage = "An error occurred reading of file system. (" + ex.toString() + ")";
            HDFS_PROCESSOR_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private String readFileSystem(FileSystem fileSystem, Path path) throws Exception {
        try (FSDataInputStream in = fileSystem.open(path)) {
            return this.getStringFromFSDataInputStream(in);
        } catch (Exception ex) {
            final String errorMessage = "An error occurred reading from file system. (" + ex.toString() + ")";
            HDFS_PROCESSOR_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private final String utf8CharsetString = StandardCharsets.UTF_8.toString();

    private String getStringFromFSDataInputStream(FSDataInputStream in) throws Exception {
        try (StringWriter stringWriter = new StringWriter()) {
            IOUtils.copy(in, stringWriter, this.utf8CharsetString);
            return stringWriter.toString();
        } catch (Exception ex) {
            final String errorMessage = "An error occurred processing of StringWriter. (" + ex.toString() + ")";
            HDFS_PROCESSOR_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    @Override
    public boolean deleteFile(String source, boolean forceDelete) {
        try (FileSystem fileSystem = FileSystem.get(URI.create(this.configuration.get(this.uriKey)), this.configuration)) {
            PathInfo pathInfo = this.checkPathAndGet(source, fileSystem);
            if (pathInfo.isNotExists()) {
                return false;
            }
            return fileSystem.delete(pathInfo.getPath(), forceDelete);
        } catch (Exception ex) {
            HDFS_PROCESSOR_LOGGER.error("An error occurred deleting of file. ({})", ex.toString());
        }
        return false;
    }

    @Override
    public boolean mkdir(String source) {
        try (FileSystem fileSystem = FileSystem.get(URI.create(this.configuration.get(this.uriKey)), this.configuration)) {
            PathInfo pathInfo = this.checkPathAndGet(source, fileSystem);
            if (pathInfo.isExists()) {
                return false;
            }
            return fileSystem.mkdirs(pathInfo.getPath());
        } catch (Exception ex) {
            HDFS_PROCESSOR_LOGGER.error("An error occurred make dir. ({})", ex.toString());
        }
        return false;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    private PathInfo checkPathAndGet(String source, FileSystem fileSystem) throws IOException {
        final Path path = new Path(source);
        final boolean fileExists = fileSystem.exists(path);
        if (!fileExists) {
            HDFS_PROCESSOR_LOGGER.debug("Target {} does not exists.", source);
        }
        return new PathInfo(path, fileExists);
    }
}
