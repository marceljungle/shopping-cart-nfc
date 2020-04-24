package vertxMain;

import database.DatabaseVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import mqtt.mqttClientVerticle;
import mqtt.mqttServerVerticle;
import telegram.telegramMain;

public class vertxMain extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		// Descomentar para ver el funcionamiento de la API Rest
		// vertx.deployVerticle(telegramMain.class.getName());
		// vertx.deployVerticle(DatabaseVerticle.class.getName());
		vertx.deployVerticle(mqttServerVerticle.class.getName());
		vertx.deployVerticle(mqttClientVerticle.class.getName());
		vertx.deployVerticle(mqttClientVerticle.class.getName());
	}
}