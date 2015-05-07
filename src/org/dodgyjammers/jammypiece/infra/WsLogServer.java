package org.dodgyjammers.jammypiece.infra;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WsLogServer extends WebSocketServer
{
  private static final Logger LOGGER = LogManager.getLogger();

  public static final WsLogServer INSTANCE = new WsLogServer();
  
  private static Object sClientConnectedSema4 = new Object();
  private static boolean sFirstClientConnected = false;
  
  public static void init() {
    System.out.println("Waiting for connection (up to 10 seconds)...");
    synchronized (sClientConnectedSema4) {
      // Just wait once, don't keep going.
      if (!sFirstClientConnected) {
        try {
          sClientConnectedSema4.wait(10000);
        } catch (InterruptedException e) {
          // ignore
        }
      }
    }
    System.out.println("Ready.");
  }
  
  private WsLogServer() {
    super(new InetSocketAddress(8887));
    start();
    System.out.println("WsLogServer started on " + getAddress() + "; view using src/external/Java-WebSocket/chat.html");
  }

  @Override
  public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onError(WebSocket arg0, Exception arg1) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onMessage(WebSocket arg0, String arg1) {
    LOGGER.warn("JammyPiece received command: " + arg1);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println("WsLogServer client connected: " + conn);
    synchronized (sClientConnectedSema4) {
      sFirstClientConnected = true;
      sClientConnectedSema4.notifyAll();
    }
  }
  
  /**
   * Sends <var>text</var> to all currently connected WebSocket clients.
   * 
   * @param text
   *            The String to send across the network.
   * @throws InterruptedException
   *             When socket related I/O errors occur.
   */
  public void sendToAll( String text ) {
          Collection<WebSocket> con = connections();
          synchronized ( con ) {
                  for( WebSocket c : con ) {
                          c.send( text );
                  }
          }
  }

}
