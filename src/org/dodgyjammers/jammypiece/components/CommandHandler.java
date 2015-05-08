package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.jammypiece;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;

public class CommandHandler extends Distributor<RichMidiEvent> implements Consumer<String> 
{
  private static final Logger LOGGER = LogManager.getLogger();
  
  @Override
  public void consume(String xiItem) throws Exception {
    if ("".equals(xiItem))
    {
      LOGGER.warn("PANIC!");
      for (int i = 0; i < 16; i++) {
        // This is intended to turn all notes off in all channels. It doesn't
        // seem to do anything though.
        distribute(new RichMidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 0x78, 0x00), -1));
      }
    }
    else if ("quit".equals(xiItem))
    {
      LOGGER.warn("QUIT");
      jammypiece.quit();
    }
  }
  
}
