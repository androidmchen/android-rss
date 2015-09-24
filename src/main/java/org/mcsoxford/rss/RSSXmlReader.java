package org.mcsoxford.rss;

import android.text.TextUtils;

import cn.adbshell.common.http.HttpUtils;
import cn.adbshell.common.util.IoUtils;
import cn.adbshell.common.util.StringUtils;

import org.apache.http.client.HttpClient;

import java.io.ByteArrayInputStream;

public class RSSXmlReader {

    /**
     * Thread-safe RSS parser SPI.
     */
    private final RSSParserSPI parser;

    /**
     * Instantiate a thread-safe HTTP client to retrieve and parse RSS feeds. Default RSS configuration capacity values
     * are used.
     */
    public RSSXmlReader() {
        this(new RSSParser(new RSSConfig()));
    }

    /**
     * Instantiate a thread-safe HTTP client to retrieve and parse RSS feeds. Internal memory consumption and load
     * performance can be tweaked with {@link RSSConfig}.
     */
    public RSSXmlReader(RSSConfig config) {
        this(new RSSParser(config));
    }

    /**
     * Instantiate a thread-safe HTTP client to retrieve RSS feeds. The injected {@link HttpClient} implementation must
     * be thread-safe.
     * 
     * @param httpclient thread-safe HTTP client implementation
     * @param parser thread-safe RSS parser SPI implementation
     */
    public RSSXmlReader(RSSParserSPI parser) {
        this.parser = parser;
    }

    /**
     * Send HTTP GET request and parse the XML response to construct an in-memory representation of an RSS 2.0 feed.
     * 
     * @param uri RSS 2.0 feed URI
     * @return in-memory representation of downloaded RSS feed
     */
    public RSSFeed load(String uri) {
        String text = HttpUtils.getUrlAsString(uri);
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(StringUtils.getBytes(text));
        RSSFeed feed = null;
        try {
            feed = parser.parse(bais);
            if (feed.getLink() == null) {
                feed.setLink(uri);
            }
        } finally {
            IoUtils.closeQuietly(bais);
        }
        return feed;
    }

}