/**
 * 
 */
package com.android.helpme.demo.rabbitMQ;

import java.io.IOException;

import android.os.RemoteException;
import android.util.Log;

import com.android.helpme.demo.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * @author Andreas Wieland
 *
 */
public class RabbitMQConsumer extends DefaultConsumer {
	private static final String LOGTAG = RabbitMQConsumer.class.getSimpleName();
	private RabbitMQService rabbitMQSerivce;
	private Channel channel;

	/**
	 * @param channel
	 */
	public RabbitMQConsumer(Channel channel, RabbitMQService service) {
		super(channel);
		this.channel = channel;
		this.rabbitMQSerivce = service;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		String string = new String(body);
		rabbitMQSerivce.sendMessage(InAppMessageType.RECEIVED_DATA, string);
		Log.i(LOGTAG, "New Message on: " +channel.toString());
	}

}
