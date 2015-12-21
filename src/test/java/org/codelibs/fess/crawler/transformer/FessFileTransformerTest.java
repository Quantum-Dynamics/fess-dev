/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.transformer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.unit.UnitFessTestCase;

public class FessFileTransformerTest extends UnitFessTestCase {

    private String encodeUrl(final String url) {
        try {
            return URLEncoder.encode(url, Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Unsupported encoding.", e);
        }
    }

    public void test_decodeUrl_ok() throws Exception {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "http://example.com/";
        exp = "http://example.com/";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://example.com/index.html";
        exp = "http://example.com/index.html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://example.com/" + encodeUrl("テスト ") + ".html";
        exp = "http://example.com/テスト .html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "file://C++.doc";
        exp = "file://C++.doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "file://C .doc";
        exp = "file://C .doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));
    }

    public void test_decodeUrl_null() throws Exception {
        final FessFileTransformer transformer = new FessFileTransformer();
        assertNull(transformer.decodeUrlAsName(null, true));
    }

    public void test_getHost_ok() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getHost(url));

        url = "http://server/home/user";
        exp = "server";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/home/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:/home/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:////server/home/user";
        exp = "server";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/" + encodeUrl("ホーム") + "/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:/" + encodeUrl("ホーム") + "/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:////" + encodeUrl("サーバー") + "/home/user";
        exp = "サーバー";
        assertEquals(exp, transformer.getHost(url));

    }

    public void test_getHost_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = null;
        exp = "";
        assertEquals(exp, transformer.getHost(url));

        url = "example:";
        exp = "unknown";
        assertEquals(exp, transformer.getHost(url));

        url = "file:";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:///";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://///";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://///example";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

    }

    public void test_getSite_ok() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "http://example.com/";
        exp = "example.com/";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "http://example.com/index.html";
        exp = "example.com/index.html";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/home/user";
        exp = "/home/user";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:/home/user";
        exp = "c:\\home\\user";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:/";
        exp = "c:\\";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:////server/user";
        exp = "\\\\server\\user";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        transformer.maxSiteLength = 10;

        url = "file:/home/user/foo";
        exp = "/home/u...";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

    }

    public void test_getSite_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "file:";
        exp = "";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file";
        exp = "file";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/";
        exp = "/";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:";
        exp = "c:";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file://";
        exp = "//";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:///";
        exp = "///";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file://///";
        exp = "\\\\\\";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));
    }
}
