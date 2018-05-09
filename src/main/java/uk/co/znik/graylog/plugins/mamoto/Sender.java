package uk.co.znik.graylog.plugins.mamoto;

import org.graylog2.plugin.Message;

public interface Sender {
    void initialize();
    void stop();
    void sendMessage(Message message);
    boolean isInitialized();
}
