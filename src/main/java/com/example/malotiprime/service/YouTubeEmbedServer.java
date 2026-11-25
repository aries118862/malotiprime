package com.example.malotiprime.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class YouTubeEmbedServer implements AutoCloseable {
    private final HttpServer server;
    private final ExecutorService executor;

    public YouTubeEmbedServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(
                InetAddress.getByName("127.0.0.1"), 0), 0);
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "maloti-youtube-embed");
            thread.setDaemon(true);
            return thread;
        });
        server.setExecutor(executor);
        server.createContext("/embed/", this::serveEmbed);
        server.start();
    }

    public URI pageUri(String videoId, boolean background) {
        if (videoId == null || !videoId.matches("[A-Za-z0-9_-]{11}")) {
            throw new IllegalArgumentException("Invalid YouTube video ID.");
        }
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort()
                + "/embed/" + videoId + (background ? "?background=1" : ""));
    }

    private void serveEmbed(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String videoId = path.substring("/embed/".length());
        if (!"GET".equals(exchange.getRequestMethod())
                || !videoId.matches("[A-Za-z0-9_-]{11}")) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }

        boolean background = "1".equals(
                exchange.getRequestURI().getQuery() == null
                        ? null
                        : exchange.getRequestURI().getQuery().replace("background=", ""));
        String pageOrigin = "http://127.0.0.1:" + server.getAddress().getPort();
        String parameters = background
                ? "?autoplay=1&mute=1&controls=0&loop=1&playlist=" + videoId
                    + "&playsinline=1&rel=0&modestbranding=1&iv_load_policy=3"
                : "?autoplay=0&controls=1&fs=0&rel=0&playsinline=1";
        parameters += "&enablejsapi=1";
        parameters += "&origin=" + URLEncoder.encode(pageOrigin, StandardCharsets.UTF_8);
        String frameStyle = background
                ? "position:absolute;top:50%;left:50%;width:max(100vw,177.78vh);"
                    + "height:max(100vh,56.25vw);transform:translate(-50%,-50%);"
                : "position:absolute;inset:0;width:100%;height:100%;";
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,"
                + "initial-scale=1,maximum-scale=1,user-scalable=no'><style>"
                + "html,body{margin:0;width:100%;height:100%;overflow:hidden;background:#000}"
                + "iframe{" + frameStyle + "border:0}</style></head><body>"
                + "<script>var frameReady=false;var pendingCommand=null;"
                + "function postPlayerCommand(command){var frame=document.getElementById('player');"
                + "if(frame&&frame.contentWindow){frame.contentWindow.postMessage("
                + "JSON.stringify({event:'command',func:command,args:[]}), '*');}}"
                + "function sendPlayerCommand(command){if(frameReady){postPlayerCommand(command);}"
                + "else{pendingCommand=command;}}"
                + "function onPlayerFrameLoad(){frameReady=true;if(pendingCommand){"
                + "postPlayerCommand(pendingCommand);pendingCommand=null;}}</script>"
                + "<iframe id='player' src='https://www.youtube-nocookie.com/embed/" + videoId + parameters
                + "' title='YouTube video player' allow='autoplay; encrypted-media; "
                + "picture-in-picture; web-share; fullscreen' referrerpolicy='origin' allowfullscreen "
                + "onload='onPlayerFrameLoad()'>"
                + "</iframe></body></html>";
        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.getResponseHeaders().set("Referrer-Policy", "origin");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(200, response.length);
        try (var body = exchange.getResponseBody()) {
            body.write(response);
        }
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
