package com.xiaot.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerEmail {

    private static final String EXCHANGE_TOPIC_INFORM = "hello-topic-inform";

    private static final String QUEUE_INFORM_EMAIL = "hello-inform-email";

    // # 匹配多个词, * 匹配一个词, 以"."分隔
    private static final String ROUTING_KEY_EMAIL = "inform.#.email.#";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_TOPIC_INFORM, BuiltinExchangeType.TOPIC);

            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_TOPIC_INFORM, ROUTING_KEY_EMAIL);

            channel.basicConsume(QUEUE_INFORM_EMAIL, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("ConsumerEmail get msg: " + new String(body));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
