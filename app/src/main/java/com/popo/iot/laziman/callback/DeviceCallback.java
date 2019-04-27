package com.popo.iot.laziman.callback;

import android.view.View;

import com.popo.laziman.cloud.iot.model.CustomDevice;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class DeviceCallback implements MqttCallback {

	@Override
	public void connectionLost(Throwable cause) {
		// Do nothing...
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String payload = new String(message.getPayload());

		System.out.println("DeviceCallback From topic: " + topic);
		System.out.println("DeviceCallback Payload : " + payload);

		// TODO: Insert your parsing / handling of the configuration
		// message here.
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Do nothing;

	}

}
