package mqtt;

import java.util.Calendar;
import java.util.Random;
import types.wifiReading;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.impl.MqttClientImpl;

public class mqttClientVerticle extends AbstractVerticle {
	private static Boolean sync = new Boolean(true);
	private String classInstanceId;

	public void start(Promise<Void> promise) {
		classInstanceId = this.hashCode() + "";
		MqttClientOptions options = new MqttClientOptions();
		options.setAutoKeepAlive(true);
		options.setAutoGeneratedClientId(false);
		options.setClientId(classInstanceId);
		options.setConnectTimeout(5000);
		options.setCleanSession(true);
		options.setKeepAliveTimeSeconds(10000);
		options.setReconnectAttempts(10);
		options.setReconnectInterval(5000);
		options.setUsername("mqttbroker");
		options.setPassword("mqttbrokerpass");
		MqttClient mqttClient = new MqttClientImpl(vertx, options);

		mqttClient.publishHandler(messageReceivedHandler -> {
			mqttMessageFormat mqttMessageFormat = new mqttMessageFormat(classInstanceId, messageReceivedHandler);
			synchronized (sync) {
				System.out.println("------------------ Begin message received ------------------");
				System.out.println(Json.encodePrettily(mqttMessageFormat));
				System.out.println("------------------ End message received ------------------");
			}
		});

		String enviar = "enviar";
		mqttClient.connect(1885, "localhost", handler -> {
			if (handler.result().code() == MqttConnectReturnCode.CONNECTION_ACCEPTED) {
				mqttClient.subscribe(mqttServerVerticle.TOPIC_WIFI, MqttQoS.AT_LEAST_ONCE.value(), handlerSubscribe -> {
					if (handlerSubscribe.succeeded()) {
						System.out.println(
								classInstanceId + " subscribed to " + mqttServerVerticle.TOPIC_WIFI + " channel");
						vertx.setPeriodic(15000, handlerPeriodic -> {
							mqttClient.publish(mqttServerVerticle.TOPIC_WIFI, Buffer.buffer(enviar),
									MqttQoS.AT_LEAST_ONCE, false, true);
						});

					} else {
						System.out.println(classInstanceId + " NOT subscribed to " + mqttServerVerticle.TOPIC_WIFI
								+ " channel " + handlerSubscribe.cause().toString());
					}
				});
			} else {
				System.out.println("Error: " + handler.result().code().toString());
			}
		});
	}
}
