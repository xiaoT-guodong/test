package com.xiaot.rabbitmq.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static String QUEUE_EMAIL = "hello-inform-email";
    private static String QUEUE_SMS = "hello-inform-sms";

    private static String EXCHANGE_FANOUT_INFORM = "hello-fanout-inform";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (Connection connection = factory.newConnection();) {
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_SMS, true, false, false, null);

            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

            channel.queueBind(QUEUE_EMAIL, EXCHANGE_FANOUT_INFORM, "");
            channel.queueBind(QUEUE_SMS, EXCHANGE_FANOUT_INFORM, "");

            String msg = "单身警告！";
            channel.basicPublish(EXCHANGE_FANOUT_INFORM, "", null, msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
