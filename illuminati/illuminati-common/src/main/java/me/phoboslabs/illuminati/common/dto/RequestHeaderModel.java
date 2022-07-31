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

package me.phoboslabs.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class RequestHeaderModel {

    private static final Logger REQUEST_HEADER_MODEL_LOGGER = LoggerFactory.getLogger(RequestHeaderModel.class);

    @Expose
    private String illuminatiProcId;
    @Expose
    private String illuminatiSProcId;
    @Expose
    private String illuminatiGProcId;

    /**
     * The Accept request-header field can be used to specify certain media types which are acceptable for the response. The
     * general syntax is as follows:
     * <p>
     * - Accept: type/subtype [q=qvalue]
     * <p>
     * Multiple media types can be listed separated by commas and the optional qvalue represents an acceptable quality level for
     * accept types on a scale of 0 to 1. Following is an example:
     * <p>
     * - Accept: text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c
     * <p>
     * This would be interpreted as text/html and text/x-c and are the preferred media types, but if they do not exist, then send
     * the text/x-dvi entity, and if that does not exist, send the text/plain entity.
     */
    @Expose
    private String accept;

    /**
     * The Accept-Charset request-header field can be used to indicate what character sets are acceptable for the response.
     * Following is the general syntax:
     * <p>
     * - Accept-Charset: character_set [q=qvalue]
     * <p>
     * Multiple character sets can be listed separated by commas and the optional qvalue represents an acceptable quality level
     * for nonpreferred character sets on a scale of 0 to 1. Following is an example:
     * <p>
     * - Accept-Charset: iso-8859-5, unicode-1-1; q=0.8
     * <p>
     * The special value "*", if present in the Accept-Charset field, matches every character set and if no Accept-Charset header
     * is present, the default is that any character set is acceptable.
     */
    @Expose
    private String acceptCharset;

    /**
     * The Accept-Encoding request-header field is similar to Accept, but restricts the content-codings that are acceptable in the
     * response. The general syntax is:
     * <p>
     * - Accept-Encoding: encoding types
     * <p>
     * Examples are as follows:
     * <p>
     * - Accept-Encoding: compress, gzip - Accept-Encoding: - Accept-Encoding: * - Accept-Encoding: compress;q=0.5, gzip;q=1.0 -
     * Accept-Encoding: gzip;q=1.0, identity; q=0.5, *;q=0
     */
    @Expose
    private String acceptEncoding;

    /**
     * The Accept-Language request-header field is similar to Accept, but restricts the set of natural languages that are
     * preferred as a response to the request. The general syntax is:
     * <p>
     * - Accept-Language: language [q=qvalue]
     * <p>
     * Multiple languages can be listed separated by commas and the optional qvalue represents an acceptable quality level for non
     * preferred languages on a scale of 0 to 1. Following is an example:
     * <p>
     * - Accept-Language: da, en-gb;q=0.8, en;q=0.7
     */
    @Expose
    private String acceptLanguage;

    /**
     * The Authorization request-header field value consists of credentials containing the authentication information of the user
     * agent for the realm of the resource being requested. The general syntax is:
     * <p>
     * - Authorization : credentials
     * <p>
     * The HTTP/1.0 specification defines the BASIC authorization scheme, where the authorization parameter is the string of
     * username:password encoded in base 64. Following is an example:
     * <p>
     * - Authorization: BASIC Z3Vlc3Q6Z3Vlc3QxMjM=
     * <p>
     * The value decodes into is guest:guest123 where guest is user ID and guest123 is the password.
     */
    @Expose
    private String authorization;

    /**
     * The Cookie request-header field value contains a name/value pair of information stored for that URL. Following is the
     * general syntax:
     * <p>
     * - Cookie: name=value
     * <p>
     * Multiple cookies can be specified separated by semicolons as follows:
     * <p>
     * - Cookie: name1=value1;name2=value2;name3=value3
     */
    @Expose
    private String cookie;

    /**
     * The Expect request-header field is used to indicate that a particular set of spring behaviors is required by the client.
     * The general syntax is:
     * <p>
     * - Expect : 100-continue | expectation-extension
     * <p>
     * If a spring receives a request containing an Expect field that includes an expectation-extension that it does not support,
     * it must respond with a 417 (Expectation Failed) status.
     */
    @Expose
    private String expect;

    /**
     * The From request-header field contains an Internet e-mail address for the human user who controls the requesting user
     * agent. Following is a simple example:
     * <p>
     * - From: webmaster@w3.org
     * <p>
     * This header field may be used for logging purposes and as a means for identifying the source of invalid or unwanted
     * requests.
     */
    @Expose
    private String from;

    /**
     * The Host request-header field is used to specify the Internet host and the port number of the resource being requested. The
     * general syntax is:
     * <p>
     * - Host : "Host" ":" host [ ":" port ] ;
     * <p>
     * A host without any trailing port information implies the default port, which is 80. For example, a request on the origin
     * spring for http://www.w3.org/pub/WWW/ would be:
     * <p>
     * - GET /pub/WWW/ HTTP/1.1 - Host: www.w3.org
     */
    @Expose
    private String host;

    /**
     * The If-Match request-header field is used with a method to make it conditional. This header requests the spring to perform
     * the requested method only if the given value in this tag matches the given entity tags represented by ETag. The general
     * syntax is:
     * <p>
     * - If-Match : entity-tag
     * <p>
     * An asterisk (*) matches any entity, and the transaction continues only if the entity exists. Following are possible
     * examples:
     * <p>
     * - If-Match: "xyzzy" - If-Match: "xyzzy", "r2d2xxxx", "c3piozzzz" - If-Match: *
     * <p>
     * If none of the entity tags match, or if "*" is given and no current entity exists, the spring must not perform the
     * requested method, and must return a 412 (Precondition Failed) response.
     */
    @Expose
    private String ifMatch;

    /**
     * The If-Modified-Since request-header field is used with a method to make it conditional. If the requested URL has not been
     * modified since the time specified in this field, an entity will not be returned from the spring; instead, a 304 (not
     * modified) response will be returned without any message-body. The general syntax of if-modified-since is:
     * <p>
     * - If-Modified-Since : HTTP-date
     * <p>
     * An example of the field is:
     * <p>
     * - If-Modified-Since: Sat, 29 Oct 1994 19:43:31 GMT
     * <p>
     * If none of the entity tags match, or if "*" is given and no current entity exists, the spring must not perform the
     * requested method, and must return a 412 (Precondition Failed) response.
     */
    @Expose
    private String ifModifiedSince;

    /**
     * The If-None-Match request-header field is used with a method to make it conditional. This header requests the spring to
     * perform the requested method only if one of the given value in this tag matches the given entity tags represented by ETag.
     * The general syntax is:
     * <p>
     * - If-None-Match : entity-tag
     * <p>
     * An asterisk (*) matches any entity, and the transaction continues only if the entity does not exist. Following are the
     * possible examples:
     * <p>
     * - If-None-Match: "xyzzy" - If-None-Match: "xyzzy", "r2d2xxxx", "c3piozzzz" - If-None-Match: *
     */
    @Expose
    private String ifNoneMatch;

    /**
     * The If-Range request-header field can be used with a conditional GET to request only the portion of the entity that is
     * missing, if it has not been changed, and the entire entity if it has been changed. The general syntax is as follows:
     * <p>
     * - If-Range : entity-tag | HTTP-date
     * <p>
     * Either an entity tag or a date can be used to identify the partial entity already received. For example:
     * <p>
     * - If-Range: Sat, 29 Oct 1994 19:43:31 GMT
     * <p>
     * Here if the document has not been modified since the given date, the spring returns the byte range given by the Range
     * header, otherwise it returns all of the new document.
     */
    @Expose
    private String ifRange;

    /**
     * The If-Unmodified-Since request-header field is used with a method to make it conditional. The general syntax is:
     * <p>
     * - If-Unmodified-Since : HTTP-date
     * <p>
     * If the requested resource has not been modified since the time specified in this field, the spring should perform the
     * requested operation as if the If-Unmodified-Since header were not present. For example:
     * <p>
     * - If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT
     * <p>
     * If the request results in anything other than a 2xx or 412 status, the If-Unmodified-Since header should be ignored.
     */
    @Expose
    private String ifUnmodifiedSince;

    /**
     * The Max-Forwards request-header field provides a mechanism with the TRACE and OPTIONS methods to limit the number of
     * proxies or gateways that can forward the request to the next inbound spring. Here is the general syntax:
     * <p>
     * - Max-Forwards : n
     * <p>
     * The Max-Forwards value is a decimal integer indicating the remaining number of times this request message may be forwarded.
     * This is useful for debugging with the TRACE method, avoiding infinite loops. For example:
     * <p>
     * - Max-Forwards : 5
     * <p>
     * The Max-Forwards header field may be ignored for all other methods defined in the HTTP specification.
     */
    @Expose
    private String maxForwards;

    /**
     * The Proxy-Authorization request-header field allows the client to identify itself (or its user) to a proxy which requires
     * authentication. Here is the general syntax:
     * <p>
     * - Proxy-Authorization : credentials
     * <p>
     * The Proxy-Authorization field value consists of credentials containing the authentication information of the user agent for
     * the proxy and/or realm of the resource being requested.
     */
    @Expose
    private String proxyAuthorization;

    /**
     * The Range request-header field specifies the partial range(s) of the content requested from the document. The general
     * syntax is:
     * <p>
     * - Range: bytes-unit=first-byte-pos "-" [last-byte-pos]
     * <p>
     * The first-byte-pos value in a byte-range-spec gives the byte-offset of the first byte in a range. The last-byte-pos value
     * gives the byte-offset of the last byte in the range; that is, the byte positions specified are inclusive. You can specify a
     * byte-unit as bytes. Byte offsets start at zero. Some simple examples are as follows:
     * <p>
     * The first 500 bytes - Range: bytes=0-499
     * <p>
     * The second 500 bytes - Range: bytes=500-999
     * <p>
     * The final 500 bytes - Range: bytes=-500
     * <p>
     * The first and last bytes only - Range: bytes=0-0,-1
     * <p>
     * Multiple ranges can be listed, separated by commas. If the first digit in the comma-separated byte range(s) is missing, the
     * range is assumed to count from the end of the document. If the second digit is missing, the range is byte n to the end of
     * the document.
     */
    @Expose
    private String range;

    /**
     * The Referer request-header field allows the client to specify the address (URI) of the resource from which the URL has been
     * requested. The general syntax is as follows:
     * <p>
     * - Referer : absoluteURI | relativeURI
     * <p>
     * Following is a simple example:
     * <p>
     * - Referer: http://www.tutorialspoint.org/http/index.htm
     * <p>
     * If the field value is a relative URI, it should be interpreted relative to the Request-URI.
     */
    @Expose
    private String referer;

    /**
     * The TE request-header field indicates what extension transfer-coding it is willing to accept in the response and whether or
     * not it is willing to accept trailer fields in a chunked transfer-coding. Following is the general syntax:
     * <p>
     * - TE   : t-codings
     * <p>
     * The presence of the keyword "trailers" indicates that the client is willing to accept trailer fields in a chunked
     * transfer-coding and it is specified either of the ways:
     * <p>
     * - TE: deflate - TE: - TE: trailers, deflate;q=0.5
     * <p>
     * If the TE field-value is empty or if no TE field is present, then only transfer-coding is chunked. A message with no
     * transfer-coding is always acceptable.
     */
    @Expose
    private String te;

    /**
     * The User-Agent request-header field contains information about the user agent originating the request. Following is the
     * general syntax:
     * <p>
     * - User-Agent : product | comment
     * <p>
     * Example:
     * <p>
     * - User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)
     */
    @Expose
    private String userAgent;

    @Expose
    private String connection;

    @Expose
    private String cacheControl;

    @Expose
    private String upgradeInsecureRequests;

    @Expose
    private String contentType;

    @Expose
    private String contentLength;

    @Expose
    private String postContentBody;

    @Expose
    private String origin;

    @Expose
    private String xRequestedWith;

    @Expose
    private String xRealIp;

    @Expose
    private String xScheme;

    @Expose
    private String xForwardedProto;

    @Expose
    private String xForwardedHost;

    @Expose
    private String xForwardedServer;

    @Expose
    private String xForwardedSsl;

    @Expose
    private String dnt;

    @Expose
    private String pragma;

    @Expose
    private String sessionInfo;

    @Expose
    private Map<String, String> anotherHeader;

    @Expose
    private Map<String, String> parsedCookie;

    public RequestHeaderModel() {
    }

    public RequestHeaderModel setRequestInfo(HttpServletRequest request) {
        if (request == null) {
            return this;
        }

        this.init(request);
        if ("post".equalsIgnoreCase(request.getMethod())) {
            try {
                this.postContentBody = StringObjectUtils.getPostBodyString(request);
            } catch (IOException ex) {
                REQUEST_HEADER_MODEL_LOGGER.error("Sorry. check your formData. ({})", ex.toString(), ex);
            }
        }

        return this;
    }


    /**
     * Get Header Info from HttpRequest - See 'RequestHeaderModel' for detailed specifications.
     *
     * @param request
     */
    private void init(HttpServletRequest request) {
        final Enumeration headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return;
        }

        while (headerNames.hasMoreElements()) {
            try {
                final String originKey = (String) headerNames.nextElement();
                final String value = request.getHeader(originKey);

                String key = originKey.toLowerCase();

                if (key.indexOf("-") > -1) {
                    final StringBuilder methodKey = new StringBuilder();
                    final String[] splitedKey = key.split("-");

                    for (int i = 0; i < splitedKey.length; i++) {
                        String partOfString = splitedKey[i];

                        if (i > 0) {
                            partOfString = partOfString.substring(0, 1).toUpperCase()
                                .concat(partOfString.substring(1));
                        }

                        methodKey.append(partOfString);
                    }

                    key = methodKey.toString();
                }

                final Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(this, value);
            } catch (Exception ex) {
                try {
                    if (this.anotherHeader == null) {
                        this.anotherHeader = new HashMap<String, String>();
                    }

                    final String key = (String) headerNames.nextElement();
                    final String value = request.getHeader(key);
                    this.anotherHeader.put(key, value);
                } catch (Exception ignore) {
                }

                REQUEST_HEADER_MODEL_LOGGER.debug(
                    "Sorry. check your header (There Exception is no problem in operation). (" + ex.toString() + ")");
            }
        }
    }

    @Deprecated
    private void getIlluminatiProcId(HttpServletRequest request) {
        final Enumeration<String> enumeration = request.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final String value = request.getAttribute(key).toString();

            if ("illuminatiProcId".equals(key)) {
                this.illuminatiProcId = value;
                break;
            }
        }
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public String getPostContentBody() {
        return this.postContentBody;
    }

    public void parsingCookie() {
        if (!StringObjectUtils.isValid(this.cookie)) {
            return;
        }

        for (String cookieData : this.cookie.split(";")) {
            final String[] tmpCookieData = cookieData.split("=");
            if (tmpCookieData.length == 2) {
                this.setParsedCookieElement(tmpCookieData[0].trim(), tmpCookieData[1].trim());
            }
        }
    }

    public void setParsedCookieElement(String key, String value) {
        if (this.parsedCookie == null) {
            this.parsedCookie = new HashMap<String, String>();
        }

        this.parsedCookie.put(key, value);
    }

    public RequestHeaderModel setSessionTransactionId(String illuminatiSProcId) {
        if (StringObjectUtils.isValid(illuminatiSProcId)) {
            this.illuminatiSProcId = illuminatiSProcId;
            return this;
        }
        return this;
    }


    public RequestHeaderModel setGlobalTransactionId(String illuminatiGProcId) {
        if (StringObjectUtils.isValid(illuminatiGProcId)) {
            this.illuminatiGProcId = illuminatiGProcId;
        }
        return this;
    }

    public RequestHeaderModel setTransactionId(String illuminatiProcId) {
        if (StringObjectUtils.isValid(illuminatiProcId)) {
            this.illuminatiProcId = illuminatiProcId;
        }
        return this;
    }

    public String getIlluminatiSProcId() {
        return this.illuminatiSProcId;
    }

    public String getIlluminatiGProcId() {
        return this.illuminatiGProcId;
    }
}
