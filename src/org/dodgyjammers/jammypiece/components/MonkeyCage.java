package org.dodgyjammers.jammypiece.components;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;

/**
 * Recording class.
 * 
 * Based on the infinite-monkeys principle, JamMy Piece will eventually produce
 * something awesome. 
 * 
 * We should have an always-on recording facility so that the latest Jam can
 * always be saved for future generations to marvel at. 
 * 
 */
public class MonkeyCage implements Consumer<RichMidiEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();
  private static final String SAVEDIR = "saved";
  
  // http://en.wikipedia.org/wiki/Pulses_per_quarter_note
  private static final int PPQ_COUNT = 384;
  
  // http://www.midi.org/aboutmidi/tut_midifiles.php
  private static final int MIDI_FILE_TYPE = 1;
  
  private static final int BACKUP_PERIOD_SEC = 10;
  
  private long mBaseTimeMillis;
  private float mMillisPerTick;
  private Sequence mSequence;
  private Track mTrack;
  private String mBasename;
  
  public MonkeyCage(TempoChangeEvent xiTempo) throws InvalidMidiDataException  {
    mBaseTimeMillis = System.currentTimeMillis();
    mSequence = new Sequence(Sequence.PPQ, PPQ_COUNT);
    mMillisPerTick = (float)xiTempo.mTempo / PPQ_COUNT / 1000;
    mTrack = mSequence.createTrack();
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");
    mBasename = MachineSpecificConfiguration.getMachineName() + "-" + fmt.format(new Date());
    LOGGER.info("Recording at " + mMillisPerTick + " millis/tick to " + mBasename);
    new BackupThread().start();
  }
  
  public void write() throws IOException
  {
    new File(SAVEDIR).mkdir();
    write(new File(SAVEDIR, mBasename + ".mid"));
  }
  
  private void write(File xiFile) throws IOException
  {
    MidiSystem.write(mSequence, MIDI_FILE_TYPE, xiFile);
    LOGGER.warn("Saved masterpiece as " + xiFile.getPath());
  }
  
  // TODO: update tempo on demand - write a tempo change event to the track and
  // update mMillisPerTick and mBaseTimeMillis.
  
  @Override
  public void consume(RichMidiEvent xiItem) throws Exception {
    long lNow = System.currentTimeMillis();
    long lTick = (long)(((float)(lNow - mBaseTimeMillis)) / mMillisPerTick);
    mTrack.add(xiItem.at(lTick));
  }
  
  private class BackupThread extends Thread {
    long mIndex = 100;
    
    @Override
    public void run() {
      while (true)
      {
        try
        {
          Thread.sleep(BACKUP_PERIOD_SEC * 1000);
          write(new File(SAVEDIR, mBasename + "-tmp-" + mIndex + ".mid"));
          mIndex++;
        }
        catch (Exception e)
        {
          LOGGER.error("Problem writing backup", e);
        }
      }
    }
  }
  
}
