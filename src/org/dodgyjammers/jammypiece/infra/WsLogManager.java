package org.dodgyjammers.jammypiece.infra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

public class WsLogManager extends OutputStreamManager {

  private static class FactoryData {
    private Layout mLayout;

    public FactoryData(Layout layout) {
      mLayout = layout;
    }
  }
  
  protected WsLogManager(OutputStream os, String streamName, Layout<?> layout) {
    super(os, streamName, layout);
  }
  
  public static WsLogManager getWsLogManager(String streamName, Layout layout) {
    return (WsLogManager)getManager(streamName, new FactoryData(layout), new ManagerFactory<WsLogManager, FactoryData>() {

      @Override
      public WsLogManager createManager(String name, FactoryData data) {
        OutputStream os = new WsLogStream();
        return new WsLogManager(os, name, data.mLayout);
      }
    });
  }
   
  public static class WsLogStream extends ByteArrayOutputStream {
    @Override
    public void flush() throws IOException {
      super.flush();
      WsLogServer.INSTANCE.sendToAll(toString());
      this.reset();
    }
  }
}
