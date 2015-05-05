package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class Metronome extends Distributor<TickEvent> implements Consumer<TempoChangeEvent>
{
  private final TimeSignatureListener mTimeSigListener;

  public Metronome(Producer<TempoChangeEvent> xiTempoSource,
                   Producer<TimeSignatureChangeEvent> xiTimeSigSource)
  {
    mTimeSigListener = null;

    xiTempoSource.registerConsumer(this);
    xiTimeSigSource.registerConsumer(mTimeSigListener);
  }

  @Override
  public void consume(TempoChangeEvent xiItem)
  {
    // Discard tempo change events.
  }

  private class TimeSignatureListener implements Consumer<TimeSignatureChangeEvent>
  {

    @Override
    public void consume(TimeSignatureChangeEvent xiItem)
    {
      // Discard time signature events.
    }
  }
}
