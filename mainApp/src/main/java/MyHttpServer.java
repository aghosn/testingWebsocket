import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

public class MyHttpServer {
    
    public static void main(String[] args) throws Exception {
       /*The HTTP server handling*/
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/index.html", new GeneralDisplay("text/html", "resources/views/controller.html"));
        server.createContext("/sample.mp4", new GeneralDisplay("application/octet-stream", "resources/videos/longer.mp4"));
        server.setExecutor(null); // creates a default executor
        server.start();
      /*The communication logic*/
      try{
       Server serv = new Server(8001);
       serv.start();
      } catch(Exception e) {
        System.out.println("Communication Server is down !");
      }
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class GeneralDisplay implements HttpHandler {
      private String type; 
      private String file;
      public GeneralDisplay(String type, String file) {
        super();
        this.type = type; 
        this.file = file;
      }
      
      public void handle(HttpExchange t) throws IOException {
        Headers h = t.getResponseHeaders(); 
        h.add("Content-Type", type);

        File file = new File(this.file);
        byte[] bytearray = new byte [(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);

        t.sendResponseHeaders(200, file.length());
        OutputStream os = t.getResponseBody();
        os.write(bytearray, 0, bytearray.length);
        os.close();
      }


    }

}
