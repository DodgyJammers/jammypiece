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
    mTimeSigListener = new TimeSignatureListener();

    xiTempoSource.registerConsumer(this);
    xiTimeSigSource.registerConsumer(mTimeSigListener);

    mWorker = new Thread(this, "Clicker");
    mWorker.setDaemon(true);
  }

  @Override
  public void consume(TempoChangeEvent xiEvent) throws Exception
  {
    mTempo = xiEvent.mTempo;
  }

  private class TimeSignatureListener implements Consumer<TimeSignatureChangeEvent>
  {

    @SuppressWarnings("synthetic-access")
    @Override
    public void consume(TimeSignatureChangeEvent xiEvent) throws Exception
    {
      mTimeSignature = xiEvent.mTimeSignature;
    }
  }

  private long GetTickInterval()
  {
    return mTempo * 1000 / mTimeSignature.mTicksPerBeat;
  }

  @Override
  public void run()
  {
    try
    {
      // Generate a steady beat.
      int lBeatNum = 0;
      int lTickNum = 0;
      long lNextTickTime = System.nanoTime() + GetTickInterval();

      while (true)
      {
        long lSleepFor = lNextTickTime - System.nanoTime();
        Thread.sleep(lSleepFor / 1000000, (int)(lSleepFor % 1000000));


        boolean lStressed;
        lStressed = ((lTickNum == 0) && (mTimeSignature.mStressPattern.charAt(lBeatNum) == '-'));
        distribute(new TickEvent(lStressed,
                                 mTimeSignature.mTicksPerBeat,
                                 lTickNum,
                                 mMidiOut.getMicrosecondPosition()));

        lTickNum = (lTickNum + 1) % mTimeSignature.mTicksPerBeat;
        if (lTickNum == 0)
          lBeatNum = (lBeatNum + 1) % mTimeSignature.mNumBeats;
        lNextTickTime += GetTickInterval();
      }
    }
    catch (InterruptedException lEx)
    {
      LOGGER.warn("Metronome interrupted", lEx);
    }
  }

  /**
   * Set the clock source, used to schedule events for output.
   *
   * @param xiClockSource - the clock source.  This should be the final MidiOut device.
   */
  public void setClockSource(MidiOut xiClockSource)
  {
    mMidiOut = xiClockSource;
  }

  @Override
  public void start()
  {
    super.start();
    mWorker.start();
  }
}
