package uk.co.znik.graylog.plugins.mamoto;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import org.graylog2.plugin.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MamotoSender implements Sender {

    private static final Logger LOG = LoggerFactory.getLogger(MamotoSender.class);
    private final Map<String, Integer> CACHE_SITE_ID = new HashMap<String, Integer>();

    private final String token;
    private final String uri;

    protected final BlockingQueue<String> queue;
    private boolean initialized = false;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public MamotoSender(String uri, String token) {
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
