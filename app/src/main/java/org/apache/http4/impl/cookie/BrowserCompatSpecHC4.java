/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http4.impl.cookie;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http4.annotation.NotThreadSafe;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http4.message.BasicHeaderValueFormatterHC4;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http4.util.Args;
import org.apache.http.util.CharArrayBuffer;


/**
 * Cookie specification that strives to closely mimic (mis)behavior of
 * common web browser applications such as Microsoft Internet Explorer
 * and Mozilla FireFox.
 *
 *
 * @since 4.0
 */
@NotThreadSafe // superclass is @NotThreadSafe
public class BrowserCompatSpecHC4 extends CookieSpecBaseHC4 {


    private static final String[] DEFAULT_DATE_PATTERNS = new String[] {
        org.apache.http4.client.utils.DateUtils.PATTERN_RFC1123,
        org.apache.http4.client.utils.DateUtils.PATTERN_RFC1036,
        org.apache.http4.client.utils.DateUtils.PATTERN_ASCTIME,
        "EEE, dd-MMM-yyyy HH:mm:ss z",
        "EEE, dd-MMM-yyyy HH-mm-ss z",
        "EEE, dd MMM yy HH:mm:ss z",
        "EEE dd-MMM-yyyy HH:mm:ss z",
        "EEE dd MMM yyyy HH:mm:ss z",
        "EEE dd-MMM-yyyy HH-mm-ss z",
        "EEE dd-MMM-yy HH:mm:ss z",
        "EEE dd MMM yy HH:mm:ss z",
        "EEE,dd-MMM-yy HH:mm:ss z",
        "EEE,dd-MMM-yyyy HH:mm:ss z",
        "EEE, dd-MM-yyyy HH:mm:ss z",
    };

    private final String[] datepatterns;

    /** Default constructor */
    public BrowserCompatSpecHC4(final String[] datepatterns, final BrowserCompatSpecFactoryHC4.SecurityLevel securityLevel) {
        super();
        if (datepatterns != null) {
            this.datepatterns = datepatterns.clone();
        } else {
            this.datepatterns = DEFAULT_DATE_PATTERNS;
        }
        switch (securityLevel) {
            case SECURITYLEVEL_DEFAULT:
                registerAttribHandler(ClientCookie.PATH_ATTR, new BasicPathHandlerHC4());
                break;
            case SECURITYLEVEL_IE_MEDIUM:
                registerAttribHandler(ClientCookie.PATH_ATTR, new BasicPathHandlerHC4() {
                        @Override
                        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                            // No validation
                        }
                    }
                );
                break;
            default:
                throw new RuntimeException("Unknown security level");
        }

        registerAttribHandler(ClientCookie.DOMAIN_ATTR, new BasicDomainHandlerHC4());
        registerAttribHandler(ClientCookie.MAX_AGE_ATTR, new BasicMaxAgeHandlerHC4());
        registerAttribHandler(ClientCookie.SECURE_ATTR, new BasicSecureHandlerHC4());
        registerAttribHandler(ClientCookie.COMMENT_ATTR, new BasicCommentHandlerHC4());
        registerAttribHandler(ClientCookie.EXPIRES_ATTR, new BasicExpiresHandlerHC4(
                this.datepatterns));
        registerAttribHandler(ClientCookie.VERSION_ATTR, new BrowserCompatVersionAttributeHandler());
    }

    /** Default constructor */
    public BrowserCompatSpecHC4(final String[] datepatterns) {
        this(datepatterns, BrowserCompatSpecFactoryHC4.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    /** Default constructor */
    public BrowserCompatSpecHC4() {
        this(null, BrowserCompatSpecFactoryHC4.SecurityLevel.SECURITYLEVEL_DEFAULT);
    }

    public List<Cookie> parse(final Header header, final CookieOrigin origin)
            throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        final String headername = header.getName();
        if (!headername.equalsIgnoreCase(SM.SET_COOKIE)) {
            throw new MalformedCookieException("Unrecognized cookie header '"
                    + header.toString() + "'");
        }
        HeaderElement[] helems = header.getElements();
        boolean versioned = false;
        boolean netscape = false;
        for (final HeaderElement helem: helems) {
            if (helem.getParameterByName("version") != null) {
                versioned = true;
            }
            if (helem.getParameterByName("expires") != null) {
               netscape = true;
            }
        }
        if (netscape || !versioned) {
            // Need to parse the header again, because Netscape style cookies do not correctly
            // support multiple header elements (comma cannot be treated as an element separator)
            final NetscapeDraftHeaderParserHC4 parser = NetscapeDraftHeaderParserHC4.DEFAULT;
            final CharArrayBuffer buffer;
            final ParserCursor cursor;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader) header).getBuffer();
                cursor = new ParserCursor(
                        ((FormattedHeader) header).getValuePos(),
                        buffer.length());
            } else {
                final String s = header.getValue();
                if (s == null) {
                    throw new MalformedCookieException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                cursor = new ParserCursor(0, buffer.length());
            }
            helems = new HeaderElement[] { parser.parseHeader(buffer, cursor) };
        }
        return parse(helems, origin);
    }

    private static boolean isQuoteEnclosed(final String s) {
        return s != null && s.startsWith("\"") && s.endsWith("\"");
    }

    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); i++) {
            final Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            final String cookieName = cookie.getName();
            final String cookieValue = cookie.getValue();
            if (cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
                BasicHeaderValueFormatterHC4.INSTANCE.formatHeaderElement(
                        buffer,
                        new BasicHeaderElement(cookieName, cookieValue),
                        false);
            } else {
                // Netscape style cookies do not support quoted values
                buffer.append(cookieName);
                buffer.append("=");
                if (cookieValue != null) {
                    buffer.append(cookieValue);
                }
            }
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }

    public int getVersion() {
        return 0;
    }

    public Header getVersionHeader() {
        return null;
    }

    @Override
    public String toString() {
        return "compatibility";
    }

}
