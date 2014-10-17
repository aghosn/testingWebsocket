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

  private void print(byte[] b) {
    System.out.println("data has size: "+b.length);
    System.out.print("{");
    for(int i = 0; i < b.length; i++) {
      System.out.print(b[i] + "; ");
    }
    System.out.println("}");
  }
  
  private int byteToUnsigned(byte b) {
    return b & 0xFF;
  }

  private int unmask(byte b) {
    return b & 0x7F;
  }

  /*Reads the length of the data
    Throwing enable clientHandler to know something is wrong
    And kill the client*/
  private int readLength(DataInputStream in) throws IOException {
    if(in == null)
      return -1;
    
    int b = unmask(in.readByte());
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
      data[i] = (byte) (data[i] ^ mask[i]);
    }
    return data;
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
