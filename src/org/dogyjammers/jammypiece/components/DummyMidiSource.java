package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.infra.Distributor;

/**
 * Component to receive MIDI events from the OS and distribute them to registered parties.
 */
public class DummyMidiSource extends Distributor<MidiEvent> implements Runnable
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final Thread mWorker;

  /**
   * Create a dummy MIDI source which creates a hard-coded set of events.
   */
  public DummyMidiSource()
  {
    LOGGER.info("Creating DummyMidiSource");
    mWorker = new Thread(this, "DummyMidiSource");
    mWorker.setDaemon(true);
  }

  public void start()
  {
    mWorker.start();
  }

  @Override
  public void run()
  {
    LOGGER.info("Starting DummyMidiSource");

    try
    {
      MidiEvent[] lEvents = {new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,  0, 60, 40), -1),
                             new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 40), -1),
                             new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,  0, 64, 40), -1),
                             new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 64, 40), -1),
                             new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,  0, 67, 40), -1),
                             new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 67, 40), -1)};

      int lEvent = 0;
      boolean lPlaying = false;

      while (true)
      {
        distribute(lEvents[lEvent]);
        lEvent = (lEvent + 1) % lEvents.length;

        lPlaying = !lPlaying;
        Thread.sleep(lPlaying ? 500 : 100, 0);
      }
    }
    catch (Exception lEx)
    {
      LOGGER.error("MidiIn died", lEx);
    }
  }
}
