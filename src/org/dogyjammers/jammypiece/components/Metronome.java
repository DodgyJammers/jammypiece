package org.dogyjammers.jammypiece.components;

import org.dogyjammers.jammypiece.events.TempoChangeEvent;
import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

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
