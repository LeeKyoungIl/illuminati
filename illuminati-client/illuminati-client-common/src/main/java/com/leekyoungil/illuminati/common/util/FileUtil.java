package com.leekyoungil.illuminati.common.util;

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

    public static String generateFileName() {
        Date nowDate = new Date();
        StringBuilder illuminatiDataFileName = new StringBuilder();
        String stringDate = FileUtil.DATE_FORMAT.format(nowDate);
        illuminatiDataFileName.append(stringDate);
        illuminatiDataFileName.append(ILLUMINATI_DATA_FILE_NAME_POSTFIX);

        return illuminatiDataFileName.toString();
    }

    public static File generateFile(String basePath, String fileName) {
        File file = new File(basePath, fileName);

        try {
            if (file.createNewFile() == true) {
                return file;
            } else if (file.exists() == true) {
                return file;
            }
        } catch (IOException e) {
            FILE_UTIL_LOGGER.error("File create error : ", e.getMessage());
            return null;
        }

        return null;
    }

    public static boolean isFileExists(String basePath, String fileName) {
        File file = new File(basePath, fileName);

        if (file.exists() == true) {
            return true;
        }

        return false;
    }

    public static void appendDataToFileByOnce(File file, String textData) {
        if (file.canWrite() == true) {
            try {
                long start = System.currentTimeMillis();
                FileWriter writer = new FileWriter(file);
                writer.write(textData);
                writer.flush();
                writer.close();
                long end = System.currentTimeMillis();
                FILE_UTIL_LOGGER.info("Time spent writing files : " + ((end - start) / 1000f) + " seconds");
            } catch (IOException e) {
                FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
            }
        } else {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
        }
    }

    public static void appendDataToFile(File file, List<String> dataList) {
        if (file.canWrite() == true) {
            try {
                FileWriter writer = new FileWriter(file);
                write(dataList, writer);
            } catch (IOException e) {
                FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
            }
        } else {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
        }
    }

    private static void write(List<String> dataList, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (String data: dataList) {
            writer.write(data);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        FILE_UTIL_LOGGER.info("Time spent writing files : " + ((end - start) / 1000f) + " seconds (" + dataList.size() + " line)");
    }

    public static boolean isDirectoryExists(String directoryName) {
        File file = new File(directoryName);

        if (file.exists() == true && file.isDirectory() == true) {
            return true;
        }

        return false;
    }

    public static boolean createDirectory(String directoryName) {
        if (isDirectoryExists(directoryName) == true) {
            FILE_UTIL_LOGGER.info(directoryName + " is already exists.");
            return true;
        }

        try {
            File file = new File(directoryName);
            return file.mkdir();
        } catch (SecurityException ex) {
            FILE_UTIL_LOGGER.info("check your dir permission.");
            return false;
        }

    }
}
