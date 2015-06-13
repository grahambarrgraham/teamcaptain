package org.rrabarg.teamcaptain.testconsole;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SuppressWarnings("unused")
public class WebserverVerticle extends Verticle {

    @Override
    public void start() {
        final Pattern chatUrlPattern = Pattern.compile("/chat/(\\w+)");
        final EventBus eventBus = vertx.eventBus();
        final Logger logger = container.logger();

        final RouteMatcher httpRouteMatcher = new RouteMatcher().get("/",
                request -> request.response().sendFile("web/chat.html")).get(".*\\.(css|js)$",
                request -> request.response().sendFile("web/" + new File(request.path())));

        JsonObject config = container.config();

        vertx.createHttpServer().requestHandler(httpRouteMatcher).listen(config.getInteger("server.port"));

        logger.info("Created vertx HTTP listener on port " + config.getInteger("server.port"));

        vertx.createHttpServer().websocketHandler(ws -> {
            final Matcher m = chatUrlPattern.matcher(ws.path());
            if (!m.matches()) {
                ws.reject();
                return;
            }

            final String chatRoom = m.group(1);
            final String id = ws.textHandlerID();
            logger.info("registering new connection with id: " + id + " for chat-room: " + chatRoom);
            vertx.sharedData().getSet("chat.room." + chatRoom).add(id);

            ws.closeHandler(event -> {
                logger.info("un-registering connection with id: " + id + " from chat-room: " + chatRoom);
                vertx.sharedData().getSet("chat.room." + chatRoom).remove(id);
            });

            ws.dataHandler(data -> {

                final ObjectMapper m1 = new ObjectMapper();
                try {
                    final JsonNode rootNode = m1.readTree(data.toString());
                    ((ObjectNode) rootNode).put("received", new Date().toString());
                    final String jsonOutput = m1.writeValueAsString(rootNode);
                    logger.info("json generated: " + jsonOutput);
                    for (final Object chatter : vertx.sharedData().getSet("chat.room." + chatRoom)) {
                        eventBus.send((String) chatter, jsonOutput);
                    }

                    if ("competition".equals(chatRoom)) {
                        eventBus.publish(ReactorVertxBridge.VERTX_BRIDGE_ADDRESS, jsonOutput);
                    }

                } catch (final IOException e) {
                    ws.reject();
                }
            });

        }).listen(config.getInteger("chat.port"));
    }
}
