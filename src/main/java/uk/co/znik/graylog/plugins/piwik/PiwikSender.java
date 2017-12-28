package uk.co.znik.graylog.plugins.piwik;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.graylog2.plugin.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PiwikSender implements Sender {

    private static final Logger LOG = LoggerFactory.getLogger(PiwikSender.class);
    private final Map<String, Integer> CACHE_SITE_ID = new HashMap<String, Integer>();

    private final String token;
    private final String uri;

    protected final BlockingQueue<String> queue;
    private boolean initialized = false;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public PiwikSender(String uri, String token) {
        this.uri = uri;
        this.token = token;
        this.queue =  new LinkedBlockingQueue<>(512);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void stop() {
        workerGroup.shutdownGracefully();
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }




}
