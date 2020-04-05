package telegram;

import java.util.HashMap;
import java.util.Map;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import types.comercio;
import types.intolerancia;
import types.producto;
import types.productosUsuario;

public class telegramMain extends AbstractVerticle {

	private TelegramBot bot;
	private String tabla;
	private String seccion;
	private MySQLPool mySQLPool;
	private Map<Integer, Object> map = new HashMap<Integer, Object>();

	@Override
	public void start(Promise<Void> future) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("DAD").setUser("dad").setPassword("dnbmusic");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);

		TelegramOptions telegramOptions = new TelegramOptions().setBotName("nfcAdminBot")
				.setBotToken("1240009222:AAFCKXVJRvKmNHgbQCgEt4S0p5dBfDQTWGw");
		bot = TelegramBot.create(vertx, telegramOptions).receiver(new LongPollingReceiver().onUpdate(handler -> {
			if (handler.getMessage().getText().toLowerCase().contains("/hola")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " �en qu� puedo ayudarte?")
						.setChatId(handler.getMessage().getChatId()));
			} else if (handler.getMessage().getText().toLowerCase().contains("/tiempo")) {
				WebClient client = WebClient.create(vertx);
				client.get(80, "api.openweathermap.org",
						"/data/2.5/forecast?id=2510911&APPID=39c0c4d7eb61c60b5809e7303412e70b&units=metric")
						.send(ar -> {
							if (ar.succeeded()) {
								HttpResponse<Buffer> response = ar.result();
								JsonObject object = response.bodyAsJsonObject();
								JsonArray list = object.getJsonArray("list");
								JsonObject lastWeather = list.getJsonObject(list.size() - 1);
								Float temp = lastWeather.getJsonObject("main").getFloat("temp");

								bot.sendMessage(new SendMessage()
										.setText("La tempertura actual en Sevilla es de " + temp + " grados")
										.setChatId(handler.getMessage().getChatId()));
							} else {
								bot.sendMessage(new SendMessage().setText("Vaya, algo ha salido mal")
										.setChatId(handler.getMessage().getChatId()));
							}
						});
			} else if (handler.getMessage().getText().toLowerCase().contains("/insertar")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName()
								+ " Indicame en que tabla quieres" + " insertar")
						.setChatId(handler.getMessage().getChatId()));
				seccion = "/insertar";
			} else if ((handler.getMessage().getText().toLowerCase().contains("comercio") && (seccion == "/insertar"))
					|| (seccion == "/insertar" && tabla == "comercio")) {
				comercio com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
				if (handler.getMessage().getText().toLowerCase().contains("comercio")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla" + " comercio, voy a proceder a preguntarte los datos.."
							+ "\n" + "Nombre del comercio?").setChatId(handler.getMessage().getChatId()));
					tabla = "comercio";
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new comercio());
				} else {
					// Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					if (com.getNombreComercio() == null) {
						com.setNombreComercio(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
						bot.sendMessage(
								new SendMessage().setText("Telefono").setChatId(handler.getMessage().getChatId()));
					} else if ((com.getTelefono() == null)) {
						com.setTelefono(Long.parseLong(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
						bot.sendMessage(new SendMessage().setText("CIF").setChatId(handler.getMessage().getChatId()));
					} else if (com.getCIF() == null) {
						map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
						mySQLPool.preparedQuery("INSERT INTO comercio (nombreComercio, telefono, CIF) VALUES (?,?,?)",
								Tuple.of(com.getNombreComercio(), com.getTelefono(), com.getCIF()), handler1 -> {
									if (handler1.succeeded()) {
										bot.sendMessage(new SendMessage()
												.setText("Se han agregado "
														+ String.valueOf(handler1.result().rowCount()) + " filas")
												.setChatId(handler.getMessage().getChatId()));
									} else {
										bot.sendMessage(new SendMessage()
												.setText("Ha ocurrido el siguiente error: "
														+ handler1.cause().toString())
												.setChatId(handler.getMessage().getChatId()));
									}
								});
						// Cuando se termine una conversacion, hay que vaciar las variables
						seccion = "";
						tabla = "";
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));
					}
				}
			} else if ((handler.getMessage().getText().toLowerCase().contains("producto") && (seccion == "/insertar"))
					|| (seccion == "/insertar" && tabla == "producto")) {
				producto prod = producto.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
				if (handler.getMessage().getText().toLowerCase().contains("producto")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla" + " producto, voy a proceder a preguntarte los datos.."
							+ "\n" + "Nombre del producto?").setChatId(handler.getMessage().getChatId()));
					tabla = "producto";
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new producto());
				} else {
					// Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					prod = producto.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					if (prod.getNombreProducto() == null) {
						prod.setNombreProducto(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Codigo Barras").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getCodigoBarras() == null) {
						prod.setCodigoBarras(Long.parseLong(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Fabricante").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getFabricante() == null) {
						prod.setFabricante(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Tel�fono").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getTelefono() == null) {
						prod.setTelefono(Long.parseLong(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						mySQLPool.preparedQuery(
								"INSERT INTO producto (nombreProducto, codigoBarras, fabricante, telefono) VALUES (?,?,?,?)",
								Tuple.of(prod.getNombreProducto(), prod.getCodigoBarras(), prod.getFabricante(),
										prod.getTelefono()),
								handler1 -> {
									if (handler1.succeeded()) {
										bot.sendMessage(new SendMessage()
												.setText("Se han agregado "
														+ String.valueOf(handler1.result().rowCount()) + " filas")
												.setChatId(handler.getMessage().getChatId()));
									} else {
										bot.sendMessage(new SendMessage()
												.setText("Ha ocurrido el siguiente error: "
														+ handler1.cause().toString())
												.setChatId(handler.getMessage().getChatId()));
									}
								});
						// Cuando se termine una conversacion, hay que vaciar las variables
						seccion = "";
						tabla = "";
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));
					}
				}
			} else if ((handler.getMessage().getText().toLowerCase().contains("intolerancia")
					&& (seccion == "/insertar")) || (seccion == "/insertar" && tabla == "intolerancia")) {
				intolerancia intoler = intolerancia.class
						.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
				if (handler.getMessage().getText().toLowerCase().contains("intolerancia")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla"
							+ " intolerancia, voy a proceder a preguntarte los datos.." + "\n" + "Nombre de la intolerancia")
							.setChatId(handler.getMessage().getChatId()));
					tabla = "intolerancia";
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new intolerancia());
				} else {
					// Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					intoler = intolerancia.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					if (intoler.getNombreIntolerancia() == null) {
						intoler.setNombreIntolerancia(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), intoler);
						
						mySQLPool.preparedQuery(
								"INSERT INTO intolerancia (nombreIntolerancia) VALUES (?)",
								Tuple.of(intoler.getNombreIntolerancia()),
								handler1 -> {
									if (handler1.succeeded()) {
										bot.sendMessage(new SendMessage()
												.setText("Se han agregado "
														+ String.valueOf(handler1.result().rowCount()) + " filas")
												.setChatId(handler.getMessage().getChatId()));
									} else {
										bot.sendMessage(new SendMessage()
												.setText("Ha ocurrido el siguiente error: "
														+ handler1.cause().toString())
												.setChatId(handler.getMessage().getChatId()));
									}
								});
						// Cuando se termine una conversacion, hay que vaciar las variables
						seccion = "";
						tabla = "";
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));
						
					}
				}
			}
		}));
		bot.start();
	}
}