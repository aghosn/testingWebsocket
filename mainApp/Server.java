import java.io.*;
import java.net.ServerSocket; 
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;

public class Server extends Thread {
  
  private ServerSocket servSocket; 
  public static final Set<Socket> connections = new CopyOnWriteArraySet<Socket>();  
  private static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

  public Server(int port) throws IOException {
    servSocket = new ServerSocket(port); 
  }

  public void run(){
    while(true) {
      try {
        Socket client = servSocket.accept();
        handShake(client);
        connections.add(client);
        ClientHandler thread = new ClientHandler(client);
        thread.start();
      } catch(IOException e) {
        System.err.println("Hoops !");
      } catch(Exception e1) {
        System.err.println("Weird: "+e1);
      }
    }
  }

  private void handShake(Socket socket) throws IOException {
    /*Handles the handShake*/
    String input;
    String buff;
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    do{
      input = in.readLine();
    } while(input != null && !input.contains("Key"));
    
    String key = input.split(" ")[1];
    
    /*Generate the Key and the response message*/
    String ansKey = getAnsKey(key); 
    String response = generateResponse(ansKey);
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    out.println(response);
  }
 
  /*Generate the response for the handshake*/
  private String generateResponse(String key) {
    String response = "HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade\nSec-WebSocket-Accept: "+key+"\n";
    return response;
  }

  /*Computes sha1 and base64 for the key answer*/
  private String getAnsKey(String key) {
    byte[] sha1 = guidSHA(key);
    byte[] base64 = Base64.encodeBase64(sha1);
    return new String(base64);
  }
  
  /*Generate the sha1 of the key::GUID*/
  private byte[] guidSHA(String key) {
    String concat = key + GUID;
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-1"); 
    } catch(Exception e) {
      e.printStackTrace();
    }
    return md.digest(concat.getBytes());
  }
 
 public static void main(String[] args) {
    try{
      Server server = new Server(8001);
      server.start();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
