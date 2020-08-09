package com.xiaot.rabbitmq.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static final String EXCHANGE_DIRECT_INFORM = "hello-direct-inform";

    private static final String QUEUE_INFORM_EMAIL = "hello-inform-email";
    private static final String QUEUE_INFORM_SMS = "hello-inform-sms";

    private static final String ROUTING_KEY_EMAIL = "email";
    private static final String ROUTING_KEY_SMS = "sms";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_DIRECT_INFORM, BuiltinExchangeType.DIRECT);

            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);

            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_DIRECT_INFORM, ROUTING_KEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_DIRECT_INFORM, ROUTING_KEY_SMS);

            channel.basicPublish(EXCHANGE_DIRECT_INFORM, ROUTING_KEY_EMAIL, null, "Email message.".getBytes());
            channel.basicPublish(EXCHANGE_DIRECT_INFORM, ROUTING_KEY_SMS, null, "SMS message.".getBytes());
            channel.basicPublish(EXCHANGE_DIRECT_INFORM, ROUTING_KEY_SMS, null, "SMS message.".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
