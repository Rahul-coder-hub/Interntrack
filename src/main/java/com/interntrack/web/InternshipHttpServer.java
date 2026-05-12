package com.interntrack.web;

import com.interntrack.service.InternService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

public class InternshipHttpServer {
    private final InternService service;
    private final int port;
    private final TemplateRenderer renderer;

    public InternshipHttpServer(InternService service, int port) {
        this.service = service;
        this.port = port;
        this.renderer = new TemplateRenderer(service);
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", this::handle);
        server.setExecutor(null);
        server.start();
        System.out.println();
        System.out.println("=================================================");
        System.out.println(" InternTrack is running");
        System.out.println(" Local link:   http://localhost:" + port);
        System.out.println(" Network link: " + findNetworkUrl());
        System.out.println(" Keep this window open while using the app.");
        System.out.println("=================================================");
        System.out.println();
    }

    private String findNetworkUrl() {
        String fallback = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                String interfaceName = (networkInterface.getName() + " " + networkInterface.getDisplayName()).toLowerCase();
                if (interfaceName.contains("virtualbox") || interfaceName.contains("vmware") || interfaceName.contains("hyper-v")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    String host = address.getHostAddress();
                    if (host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") && !host.startsWith("169.254.")) {
                        String url = "http://" + host + ":" + port;
                        if (interfaceName.contains("wi-fi") || interfaceName.contains("wireless") || interfaceName.contains("wlan")) {
                            return url;
                        }
                        if (fallback == null) {
                            fallback = url;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            return "http://YOUR_IPV4_ADDRESS:" + port;
        }
        return fallback == null ? "http://YOUR_IPV4_ADDRESS:" + port : fallback;
    }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/static/style.css")) {
                sendCss(exchange);
            } else if (method.equals("GET") && path.equals("/")) {
                Map<String, String> query = HttpUtil.parseQuery(exchange.getRequestURI().getQuery());
                sendHtml(exchange, renderer.dashboard(query.getOrDefault("q", "")));
            } else if (method.equals("GET") && path.equals("/interns/new")) {
                sendHtml(exchange, renderer.newInternForm(""));
            } else if (method.equals("POST") && path.equals("/interns")) {
                service.addIntern(HttpUtil.parseForm(exchange));
                HttpUtil.redirect(exchange, "/");
            } else if (method.equals("POST") && path.matches("/interns/\\d+/status")) {
                int id = extractId(path);
                service.changeStatus(id, HttpUtil.parseForm(exchange).getOrDefault("reviewStatus", "Scheduled"));
                HttpUtil.redirect(exchange, "/");
            } else if (method.equals("POST") && path.matches("/interns/\\d+/delete")) {
                service.deleteIntern(extractId(path));
                HttpUtil.redirect(exchange, "/");
            } else {
                send(exchange, 404, "text/plain", "Page not found");
            }
        } catch (IllegalArgumentException ex) {
            sendHtml(exchange, renderer.newInternForm(ex.getMessage()));
        } catch (Exception ex) {
            send(exchange, 500, "text/plain", "Server error: " + ex.getMessage());
        }
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }

    private void sendCss(HttpExchange exchange) throws IOException {
        String css = new String(InternshipHttpServer.class.getResourceAsStream("/static/style.css").readAllBytes(), StandardCharsets.UTF_8);
        send(exchange, 200, "text/css; charset=UTF-8", css);
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        send(exchange, 200, "text/html; charset=UTF-8", html);
    }

    private void send(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
