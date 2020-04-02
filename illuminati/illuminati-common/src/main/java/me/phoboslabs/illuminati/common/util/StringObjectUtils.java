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

package me.phoboslabs.illuminati.common.util;

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
        return value != null && value.trim().length() > 0;
    }

    public static boolean isNotValid(final String value) {
        return !isValid(value);
    }

    public static byte[] gzipMessage(final String message) throws Exception {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                GZIPOutputStream stream = new GZIPOutputStream(bos);
                ) {
            byte[] bytes;
            try {
                bytes = message.getBytes(IlluminatiConstant.BASE_CHARSET);
            } catch (UnsupportedEncodingException e) {
                STRINGUTIL_LOGGER.error("No UTF-8 support available. ("+e.toString()+")");
                throw new RuntimeException("No UTF-8 support available.", e);
            }
            stream.write(bytes);
            stream.finish();

            return bos.toByteArray();
        } catch (IOException e) {
            throw new Exception(e.getCause().getMessage());
        }
    }

    public static String decompressGzip (final byte[] compressed) throws Exception {
        if (compressed == null || compressed.length == 0) {
            throw new Exception("compressed byte array must not be null.");
        }

        final StringBuilder outStr = new StringBuilder();
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, IlluminatiConstant.BASE_CHARSET));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }

            gis.close();
            bufferedReader.close();
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
        writer.close();

        byte[] returnByteArray = bos.toByteArray().clone();
        bos.close();

        return new String(returnByteArray, IlluminatiConstant.BASE_CHARSET);
    }

    public static String getExceptionMessageChain (Throwable throwable) {
        final StringBuilder result = new StringBuilder()
                                        .append("[IlluminatiException] : An exception occurred while running")
                                        .append("\r\n\r\n");

        ///["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
        while (throwable != null) {
            result.append(throwable.toString());
            throwable = throwable.getCause();
        }

        return result.toString();
    }

    public static String removeDotAndUpperCase (final String value) throws Exception {
        if (!isValid(value)) {
            throw new Exception("value must not be null.");
        }

        final StringBuilder returnValue = new StringBuilder(value);

        for (int i=0; i<value.length(); i++) {
            if (value.charAt(i) == '.' && i < value.length()) {
                returnValue.setCharAt(i+1, Character.toUpperCase(value.charAt(i+1)));
            }
        }

        return returnValue.toString().replace(".", "");
    }

    public static String objectToString (final Object object) throws Exception {
        if (object == null) {
            throw new Exception("object must not be null.");
        }

        try(StringWriter stringWriter = new StringWriter()) {
            IlluminatiConstant.BASIC_OBJECT_STRING_MAPPER.writeValue(stringWriter, object);
            final String resultString = stringWriter.toString();
            return resultString.replaceAll(System.getProperty("line.separator"), "");
        } catch (IOException ex) {
            final String errorMessage = "Sorry. had a error on during Object to String. ("+ex.toString()+")";
            STRINGUTIL_LOGGER.info(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private static boolean isDeleteKeywordArrayValidated(final String[] deleteKeyword, final int deleteKeywordLocationIndexLength) {
        return deleteKeyword != null && deleteKeyword.length > 0 && deleteKeyword.length == deleteKeywordLocationIndexLength;
    }

    public static String deleteKeywordInString (String origin, final String[] deleteKeyword, final int[] deleteKeywordLocationIndex) {
        if (StringObjectUtils.isValid(origin) && isDeleteKeywordArrayValidated(deleteKeyword, deleteKeywordLocationIndex.length)) {
            for (int i=0; i<deleteKeyword.length; i++) {
                if (origin.indexOf(deleteKeyword[i]) != deleteKeywordLocationIndex[i]) {
                    continue;
                }

                origin = origin.replace(deleteKeyword[i], "");
            }
        }

        return origin;
    }

    public static byte[] encode (final char[] charArray) throws Exception {
        try {
            final CharsetEncoder encoder = Charset.forName(IlluminatiConstant.BASE_CHARSET).newEncoder();
            final ByteBuffer bb = encoder.encode(CharBuffer.wrap(charArray));

            final byte[] ba=new byte[bb.limit()];
            bb.get(ba);

            return ba;
        } catch (CharacterCodingException ex) {
            final String errorMessage = "Sorry. had a error on during string encode. ("+ex.getCause().getMessage()+")";
            STRINGUTIL_LOGGER.error(errorMessage, ex);
            throw new Exception(errorMessage);
        }
    }

    public static String generateId (final long idTimestamp, final String postfix) {
        final StringBuilder id = new StringBuilder();
        id.append(UUID.randomUUID().toString().replace("-", ""));
        id.append(idTimestamp);

        if (StringObjectUtils.isValid(postfix)) {
            id.append("-");
            id.append(postfix);
        }

        return id.toString();
    }

    public static String convertFirstLetterToLowerlize (final String str) {
        StringBuilder convertString = new StringBuilder()
                                        .append(str.substring(0, 1).toLowerCase())
                                        .append(str.substring(1));
        return convertString.toString();
    }
}
