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
    mWorker.start();
  }

  @Override
  public void run()
  {
    int[] lNotes = {60, 64, 67};
    int lNote = 0;

    LOGGER.info("Starting DummyMidiSource");
    try
    {
      boolean lPlaying = false;

      while (true)
      {
        Thread.sleep(1000);
        distribute(new MidiEvent(new ShortMessage(lPlaying ? ShortMessage.NOTE_OFF : ShortMessage.NOTE_ON,
                                                  0,
                                                  lNotes[lNote],
                                                  40), -1));
        lPlaying = !lPlaying;
        if (!lPlaying)
        {
          lNote = (lNote + 1) % lNotes.length;
        }
      }
    }
    catch (Exception lEx)
    {
      System.err.println("MidiIn died");
      lEx.printStackTrace();
    }
  }
}
