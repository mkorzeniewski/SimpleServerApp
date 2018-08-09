package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;


public class MyFirstVerticle extends AbstractVerticle {
    //Store our product
    private Map<Integer, Whisky> product = new LinkedHashMap<>();
    //Create some product
    private void createSomeData(){
        Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
        product.put(bowmore.getId(),bowmore);
        Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
        product.put(talisker.getId(),talisker);
    }


    @Override
    public void start(Future<Void> fut) {
        createSomeData();

        //create a router object
        Router router = Router.router(vertx);



        // Bind "/" to our hello message - so we are still compatible
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type","text/html")
                    .end("<h1> Hello from my first Vert.x 3 application </h1>");

        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        router.get("/api/whiskies").handler(this::getAll);
        router.route("/api/whiskies*").handler(BodyHandler.create());
        router.post("/api/whiskies").handler(this::addOne);


        //Create the HTTP server and pass the "accept" method to the request handler
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration
                        // default to 8080.
                        config().getInteger("http.port",8080),
                        result -> {
                            if (result.succeeded()){
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        });
    }

    private void addOne(RoutingContext routingContext) {
        final  Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(),Whisky.class);

    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type","application/json; charset=utf-8")
                .end(Json.encodePrettily(product.values()));
    }
}
