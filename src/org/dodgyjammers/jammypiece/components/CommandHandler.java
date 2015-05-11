package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

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
      allNotesOff();
    }
    else if ("quit".equals(xiItem))
    {
      LOGGER.warn("QUIT");
      allNotesOff();
      jammypiece.quit();
    }
    else if ("gm".equals(xiItem))
    {
      LOGGER.warn("GM - General MIDI on");
      SysexMessage lgmon = new SysexMessage(new byte[]{ (byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7 }, 6);
      distribute(new RichMidiEvent(lgmon, -1));
    }
  }

  private void allNotesOff() throws Exception
  {
    for (int i = 0; i < 16; i++) {
      distribute(new RichMidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 0x78, 0x00), -1));
    }
  }
}
