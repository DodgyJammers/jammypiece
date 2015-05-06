package org.dodgyjammers.jammypiece.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.TimeSignature;

public class Metronome extends Distributor<TickEvent> implements Consumer<TempoChangeEvent>, Runnable
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final TimeSignatureListener mTimeSigListener;
  private MidiOut mMidiOut;

  private volatile TimeSignature mTimeSignature = TimeSignature.COMMON_TIME;
  private volatile long mTempo = 500000;

  private final Thread mWorker;

  public Metronome(Producer<TempoChangeEvent> xiTempoSource,
                   Producer<TimeSignatureChangeEvent> xiTimeSigSource)
  {
    mTimeSigListener = null;

    xiTempoSource.registerConsumer(this);
    xiTimeSigSource.registerConsumer(mTimeSigListener);

    mWorker = new Thread(this, "Clicker");
    mWorker.setDaemon(true);
  }

  @Override
  public void consume(TempoChangeEvent xiEvent)
  {
    mTempo = xiEvent.mTempo;
  }

  private class TimeSignatureListener implements Consumer<TimeSignatureChangeEvent>
  {

    @Override
    public void consume(TimeSignatureChangeEvent xiEvent)
    {
      mTimeSignature = xiEvent.mTimeSignature;
    }
  }

  @Override
  public void run()
  {
    try
    {
      // Generate a steady beat.
      int lBeatNum = 0;
      long lNextTickTime = System.nanoTime() + (mTempo * 1000);

      while (true)
      {
        long lSleepFor = lNextTickTime - System.nanoTime();
        Thread.sleep(lSleepFor / 1000000, (int)(lSleepFor % 1000000));

        if (mTimeSignature.mStressPattern.charAt(lBeatNum) == '-')
        {
          distribute(new TickEvent(true, mMidiOut.getMicrosecondPosition()));
        }
        else
        {
          distribute(new TickEvent(false, mMidiOut.getMicrosecondPosition()));
        }

        lBeatNum = (lBeatNum + 1) % mTimeSignature.mNumBeats;
        lNextTickTime += mTempo * 1000;
      }
    }
    catch (InterruptedException lEx)
    {
      LOGGER.warn("Metronome interrupted", lEx);
    }
  }

  public void setClockSource(MidiOut xiClockSource)
  {
    mMidiOut = xiClockSource;
    mWorker.start();
  }
}
