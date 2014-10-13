import java.net.Socket;
import java.io.*;

public class ClientHandler extends Thread {
  private static final byte[] msg = new byte[]{-127, 2, 1, 1};
  private static Socket client; 

  public ClientHandler(Socket socket) {
    client = socket;
  }

  /*The run function simply listens for incoming message,
    If it receives one, it sends one to everyone        */
  public void run() {
    int input;
    byte[] mybyte = new byte[7]; //TODO magic number should change that
    try {
      DataInputStream in = new DataInputStream(client.getInputStream());
      do { 
        in.read(mybyte); 
        broadcast();
      } while(!client.isClosed());
    } catch(IOException e) {
      /**/
    } finally {
      /*Remove the socket*/
      try{
        client.close();
        Server.connections.remove(client);
      }catch(Exception e1) {};
    }
  }

  /*Broadcast message to the other clients*/
  /*TODO remove the static synchronized => already handles concurrency*/
  private static synchronized void broadcast() {
    DataOutputStream out;
    for( Socket s: Server.connections) {
      try {
         out = new DataOutputStream(s.getOutputStream());
         out.write(msg);
         out.flush();
      } catch(Exception e) {
        /*TODO do something meaningful... oh bretzels !*/
      }
    }
  }
}
