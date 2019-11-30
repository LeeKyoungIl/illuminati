package me.phoboslabs.illuminati.common.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileUtil {

    private final static Logger FILE_UTIL_LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String ILLUMINATI_DATA_FILE_NAME_POSTFIX = "_illuminati_data.log";

    private static final String ENCODING = "utf-8";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String generateFileName() {
        return new StringBuilder().append(FileUtil.DATE_FORMAT.format(new Date()))
                .append(ILLUMINATI_DATA_FILE_NAME_POSTFIX).toString();
    }

    public static File generateFile(String basePath, String fileName) throws Exception {
        final File file = new File(basePath, fileName);

        try {
            if (file.exists()) {
                return file;
            } else if (file.createNewFile()) {
                return file;
            }

            final String errorMessage = "File create error";
            FILE_UTIL_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        } catch (IOException e) {
            final String errorMessage = "File create error : ".concat(e.getMessage());
            FILE_UTIL_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    public static boolean isFileExists(String basePath, String fileName) {
        return new File(basePath, fileName).exists();
    }

    public static void appendDataToFileByOnce(File file, String textData) {
        if (file.canWrite() == false) {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
            return;
        }

        try {
            final long start = System.currentTimeMillis();
            FileWriter writer = new FileWriter(file, true);
            writer.append(textData);
            writer.flush();
            writer.close();

            FILE_UTIL_LOGGER.info("Time spent writing files : " + ((System.currentTimeMillis() - start) / 1000f) + " seconds");
        } catch (IOException e) {
            FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
        }
    }

    public static void appendDataToFile(File file, List<String> dataList) {
        if (file.canWrite() == false) {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
            return;
        }

        try {
            FileWriter writer = new FileWriter(file, true);
            final long start = System.currentTimeMillis();
            for (String data: dataList) {
                writer.append(data.concat(LINE_SEPARATOR));
            }
            writer.flush();
            writer.close();

            FILE_UTIL_LOGGER.info("Time spent writing files : " + ((System.currentTimeMillis() - start) / 1000f) + " seconds (" + dataList.size() + " line)");
        } catch (IOException e) {
            FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
        }
    }

    public static boolean isDirectoryExists(String directoryName) {
        final File file = new File(directoryName);
        return file.exists() && file.isDirectory();
    }

    public static boolean createDirectory(String directoryName) {
        if (isDirectoryExists(directoryName)) {
            FILE_UTIL_LOGGER.info(directoryName + " is already exists.");
            return true;
        }

        try {
            return new File(directoryName).mkdir();
        } catch (SecurityException ex) {
            FILE_UTIL_LOGGER.info("check your dir permission.");
            return false;
        }
    }

    public static List<String> getDataFromFile (File fileOb) throws Exception {
        try {
            return FileUtils.readLines(fileOb, ENCODING);
        } catch (IOException e) {
            final String errorMessage = "check your file.".concat(e.getMessage());
            FILE_UTIL_LOGGER.info(errorMessage);
            throw new Exception(errorMessage);
        }
    }
}
