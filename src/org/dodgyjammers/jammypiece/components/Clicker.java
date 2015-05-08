package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Clicker component, converts internal metronome ticks into audible events.
 */
public class Clicker extends Distributor<RichMidiEvent> implements Consumer<TickEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();

  private static final boolean ENABLED = MachineSpecificConfiguration.getCfgVal(CfgItem.CLICKER_ENABLED, false);
  private static final int CHANNEL = MachineSpecificConfiguration.getCfgVal(CfgItem.CLICKER_CHANNEL, 0);
  private static final int STRESSED = MachineSpecificConfiguration.getCfgVal(CfgItem.CLICKER_STRESSED_NOTE, 71);
  private static final int UNSTRESSED = MachineSpecificConfiguration.getCfgVal(CfgItem.CLICKER_UNSTRESSED_NOTE, 77);

  public Clicker(Producer<TickEvent> xiMetronome)
  {
    xiMetronome.registerConsumer(this);
  }

  @Override
  public void consume(TickEvent xiTick) throws Exception
  {
    try
    {
      if (ENABLED)
      {
        if (xiTick.mStress)
        {
          distribute(new RichMidiEvent(new ShortMessage(ShortMessage.NOTE_ON, CHANNEL, STRESSED, 40), -1));
          //distribute(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, CHANNEL, STRESSED, 40), xiTick.mTimestamp + 100000));
        }
        else if (xiTick.mTickInBeat == 0)
        {
          distribute(new RichMidiEvent(new ShortMessage(ShortMessage.NOTE_ON, CHANNEL, UNSTRESSED, 40), -1));
          //distribute(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, CHANNEL, UNSTRESSED, 40), xiTick.mTimestamp + 100000));
        }
      }
    }
    catch (InvalidMidiDataException lEx)
    {
      LOGGER.error("Failed to send click event", lEx);
    }
  }
}
