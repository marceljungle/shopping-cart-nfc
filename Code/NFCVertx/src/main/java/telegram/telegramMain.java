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
import types.comercio;

import org.schors.vertx.telegram.bot.api.types.Update;

public class telegramMain extends AbstractVerticle{
	
	private TelegramBot bot;
	private String tabla;
	private String seccion;
	private  Map<Integer,Object> map = new HashMap<Integer, Object>();
	
	@Override
	public void start(Promise<Void> future) {
		TelegramOptions telegramOptions = new TelegramOptions()
				.setBotName("nfcAdminBot")
				.setBotToken("1240009222:AAFCKXVJRvKmNHgbQCgEt4S0p5dBfDQTWGw");
		bot = TelegramBot.create(vertx, telegramOptions)
				.receiver(new LongPollingReceiver().onUpdate(handler -> {
					if (handler.getMessage().getText().toLowerCase().contains("/hola")) {
						bot.sendMessage(new SendMessage()
								.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " �en qu� puedo ayudarte?")
								.setChatId(handler.getMessage().getChatId()));
					}else if (handler.getMessage().getText().toLowerCase().contains("/tiempo")) {
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
								}else{
									bot.sendMessage(new SendMessage()
											.setText("Vaya, algo ha salido mal")
											.setChatId(handler.getMessage().getChatId()));
								}
							});
					}else if(handler.getMessage().getText().toLowerCase().contains("/insertar")) {
						bot.sendMessage(new SendMessage()
								.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " Indicame en que tabla quieres"
										+ " insertar")
								.setChatId(handler.getMessage().getChatId()));	
						seccion = "insertar";
						map.put(handler.getMessage().getChatId(),  new comercio());

					}else if((handler.getMessage().getText().toLowerCase().contains("comercio") && (seccion == "/insertar") || 
							tabla == "comercio")) {
						if(handler.getMessage().getText().toLowerCase().contains("comercio")) {
						bot.sendMessage(new SendMessage()
								.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " Has seleccionado la tabla"
										+ " comercio, voy a proceder a preguntarte los datos.." + "\n" + "Nombre del comercio?")
								.setChatId(handler.getMessage().getChatId()));
						tabla = "comercio";
						}else {
							if(map.) {
								
							}
						}
					}
				}));
		bot.start();
	}
	
	public void comercioBot(Update handler) {
				
				String nombreComercio = handler.getMessage().getText().toLowerCase();
				
				bot.sendMessage(new SendMessage()
						.setText("Tel�fono?"));
				String telefono = handler.getMessage().getText().toLowerCase();
				bot.sendMessage(new SendMessage()
						.setText("CIF?"));
				String CIF = handler.getMessage().getText().toLowerCase();
				bot.sendMessage(new SendMessage()
						.setText("Vale, de acuerdo, son estos los datos que quieres introducir?" + 
				"\n" + "Nombre: " + nombreComercio + "Telefono: " + telefono + "CIF: " + CIF));
	}
	
	
	
	
	
	
	
}