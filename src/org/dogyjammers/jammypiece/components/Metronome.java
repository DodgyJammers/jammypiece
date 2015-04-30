package org.dogyjammers.jammypiece.components;

import org.dogyjammers.jammypiece.events.TempoChangeEvent;
import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class Metronome extends Distributor<TickEvent> implements Consumer<TempoChangeEvent>
{
  public Metronome(Producer<TempoChangeEvent> xiTempoSource)
  {
    xiTempoSource.registerConsumer(this);
  }

  @Override
  public void consume(TempoChangeEvent xiItem)
  {
    // Discard tempo change events.
  }
}
