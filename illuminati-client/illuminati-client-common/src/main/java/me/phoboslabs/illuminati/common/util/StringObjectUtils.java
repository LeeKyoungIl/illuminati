package me.phoboslabs.illuminati.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class StringObjectUtils {

    private final static Logger STRINGUTIL_LOGGER = LoggerFactory.getLogger(StringObjectUtils.class);

    public static boolean isValid (final String value) {
        if (value == null || value.equals("")) {
            return false;
        }

        return true;
    }

    public static byte[] gzipMessage(final String message) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            GZIPOutputStream stream = new GZIPOutputStream(bos);
            byte[] bytes;
            try {
                bytes = message.getBytes(IlluminatiConstant.BASE_CHARSET);
            } catch (UnsupportedEncodingException e) {
                STRINGUTIL_LOGGER.error("No UTF-8 support available. ("+e.toString()+")");
                throw new RuntimeException("No UTF-8 support available.", e);
            }
            stream.write(bytes);
            stream.finish();
            stream.close();
            byte[] zipped = bos.toByteArray();
            bos.close();
            return zipped;
        } catch (IOException e) {
            return null;
        }
    }

    public static String decompressGzip (final byte[] compressed) throws IOException {
        final StringBuilder outStr = new StringBuilder();
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, IlluminatiConstant.BASE_CHARSET));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }
        } else {
            outStr.append(compressed);
        }
        return outStr.toString();
    }

    public static boolean isCompressed (final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static String getPostBodyString (HttpServletRequest request) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(bos, IlluminatiConstant.BASE_CHARSET);
        final Map<String, String[]> form = request.getParameterMap();

        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            List<String> values = Arrays.asList(form.get(name));

            for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
                final String value = valueIterator.next();
                writer.write(URLEncoder.encode(name, IlluminatiConstant.BASE_CHARSET));

                if (value != null) {
                    writer.write('=');
                    writer.write(URLEncoder.encode(value, IlluminatiConstant.BASE_CHARSET));

                    if (valueIterator.hasNext()) {
                        writer.write('&');
                    }
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&');
            }
        }
        writer.flush();

        return new String(bos.toByteArray(), IlluminatiConstant.BASE_CHARSET);
    }

    public static String getExceptionMessageChain (Throwable throwable) {
        final StringBuilder result = new StringBuilder();
        result.append("[IlluminatiException] : An exception occurred while running");
        result.append("\r\n\r\n");

        ///["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
        while (throwable != null) {
            result.append(throwable.toString());
            throwable = throwable.getCause();
        }

        return result.toString();
    }

    public static String removeDotAndUpperCase (final String value) {
        if (!isValid(value)) {
            return null;
        }

        final StringBuilder returnValue = new StringBuilder(value);

        for (int i=0; i<value.length(); i++) {
            if (value.charAt(i) == '.' && i < value.length()) {
                returnValue.setCharAt(i+1, Character.toUpperCase(value.charAt(i+1)));
            }
        }

        return returnValue.toString().replace(".", "");
    }

    public static String objectToString (final Object object) {
        if (object == null) {
            return null;
        }

        try {
            final StringWriter stringWriter = new StringWriter();

            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.writeValue(stringWriter, object);

            return stringWriter.toString().replaceAll(System.getProperty("line.separator"), "");
        } catch (IOException ex) {
            STRINGUTIL_LOGGER.info("Sorry. had a error on during Object to String. ("+ex.toString()+")");
            return null;
        }
    }

    public static String deleteKeywordInString (String origin, final String[] deleteKeyword, final int[] deleteKeywordLocationIndex) {
        if (StringObjectUtils.isValid(origin) && deleteKeyword != null && deleteKeyword.length > 0
                && deleteKeyword.length == deleteKeywordLocationIndex.length) {
            for (int i=0; i<deleteKeyword.length; i++) {
                if (origin.indexOf(deleteKeyword[i]) == deleteKeywordLocationIndex[i]) {
                    origin = origin.replace(deleteKeyword[i], "");
                }
            }
        }

        return origin;
    }

    public static byte[] encode (final char[] charArray){
        try {
            final CharsetEncoder encoder = Charset.forName(IlluminatiConstant.BASE_CHARSET).newEncoder();
            final ByteBuffer bb = encoder.encode(CharBuffer.wrap(charArray));

            final byte[] ba=new byte[bb.limit()];
            bb.get(ba);

            return ba;
        } catch (CharacterCodingException ex) {
            STRINGUTIL_LOGGER.error("Sorry. had a error on during string encode. ("+ex.toString()+")");
            return null;
        }
    }

    public static String generateId (final long idTimestamp, final String postfix) {
        final StringBuilder id = new StringBuilder();
        id.append(UUID.randomUUID().toString().replace("-", ""));
        id.append(String.valueOf(idTimestamp));

        if (StringObjectUtils.isValid(postfix)) {
            id.append("-");
            id.append(postfix);
        }

        return id.toString();
    }

    public static String convertFirstLetterToLowerize (String str) {
        StringBuilder convertString = new StringBuilder();
        convertString.append(str.substring(0, 1).toLowerCase());
        convertString.append(str.substring(1));
        return convertString.toString();
    }
}
