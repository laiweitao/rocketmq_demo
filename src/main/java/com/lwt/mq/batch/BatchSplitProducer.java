package com.lwt.mq.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Split into lists
 * The complexity only grow when you send large batch and you may not sure if it exceeds the size limit (1MiB).
 *
 * At this time, you’d better split the lists:
 */
public class BatchSplitProducer {
    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducerGroup");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setInstanceName("BatchProducer");
        //Launch the instance.
        producer.start();
        String topic = "BatchTopic";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID003", "Hello world 2".getBytes()));
        try {
            //then you could split the large list into small ones:
            ListSplitter splitter = new ListSplitter(messages);
            while (splitter.hasNext()) {
                List<Message>  listItem = splitter.next();
                producer.send(listItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //handle the error
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}