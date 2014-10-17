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
    try {
      DataInputStream in = new DataInputStream(client.getInputStream());
      do {
        /*Discard the first byte*/
        in.readByte();
        int length = readLength(in);
        if(length == -1)
          throw new IOException();

        byte[] mask = readMask(in);
        byte[] data = readData(length, mask, in);
        print(data);
        
        MessageWrapper msg = interpretData(data);
        System.out.println("Message "+msg.order +" : "+msg.time);
        broadcast();
      } while(!client.isClosed());
    } catch(IOException e) {
      /**/
    } catch(Exception e) {
      System.err.println("Error: ClientHandler received ");
      e.printStackTrace();
    } finally {
      /*Remove the socket*/
      try{
        client.close();
        Server.connections.remove(client);
      }catch(Exception e1) {};
    }
  }
  
  private void print(byte[] b) {
    System.out.println("data has size: "+b.length);
    System.out.print("{");
    for(int i = 0; i < b.length; i++) {
      System.out.print(b[i] + "; ");
    }
    System.out.println("}");
  }

  /*Reads the length of the data
    Throwing enable clientHandler to know something is wrong
    And kill the client*/
  private int readLength(DataInputStream in) throws IOException {
    if(in == null)
      return -1;
    
    int b = in.readByte() & 0x7F;
    /*Length fits in one byte*/
    if(b < 126) 
      return b; 
     
    /*We do not handle larger sizes*/
    return -1;            
  }

  /*Reads the mask in the data*/
  private byte[] readMask(DataInputStream in) throws IOException {
    if(in == null)
      return null;
    
    byte[] mask = new byte[4];
    in.read(mask);
    return mask;
  }
  
  /*Reads the data and unmasks it*/
  private byte[] readData(int length, byte[] mask, DataInputStream in) throws IOException {
    if(length < 0 || in == null || mask == null || mask.length != 4)
      return null;
    
    byte[] data = new byte[length];
    in.read(data);
    for(int i = 0; i < length; i++) { 
      data[i] = (byte) (data[i] ^ mask[i % 4]);
    }
    return data;
  }
 
  /*Responsible for reading the Data correctly*/
  private MessageWrapper interpretData(byte[] data) throws Exception {
    /*Data is either play of [play;pause]double */
    if(data == null || (data.length < "play".length()+8 && data.length != "play".length()+1))
      throw new Exception("--Data is too short "+ data.length +"!--");

    String msg = new String(data);
    System.out.println("The message received --"+msg+"--"); 
    
    if(msg.equals("play0")){
      return new MessageWrapper(Order.Play, 0);
    }

    /*TODO Check for double size !*/
    if(msg.substring(0, "pause".length()).equals("pause") && msg.length() == 13) {
     String time = msg.substring("pause".length());
     return (new MessageWrapper(Order.Pause, Double.parseDouble(time)));
    }

    if(msg.substring(0, "play".length()).equals("play") && msg.length() == 12) {
      String time = msg.substring("play".length());
      return (new MessageWrapper(Order.Play, Double.parseDouble(time)));
    }
    
    throw new Exception("--Data doesn't respect the format !--");
    
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

  /*Wrapps the content of client's side message*/
  static class MessageWrapper {
    Order order;
    double time;

    public MessageWrapper(Order order, double time) {
      this.order = order; 
      this.time = time;
    } 
  }
  
  /*Possible orders*/
  static enum Order {
    Pause, 
    Play;
  }
}
