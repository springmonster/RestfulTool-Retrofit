package core.service;

import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.Nullable;

/**
 * @version 1.0
 * @Author KuangHaochuan
 */
public interface RestTopic {

    Topic<RestTopic> ACTION_SCAN_SERVICE = Topic.create("RestTopic.ACTION_SCAN_SERVICE", RestTopic.class);

    /**
     * after
     *
     * @param data data
     */
    void afterAction(@Nullable Object data);
}
