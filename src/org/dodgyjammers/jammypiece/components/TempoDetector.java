package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class TempoDetector extends Distributor<TempoChangeEvent> implements Consumer<RichMidiEvent>
{
  private TempoChangeEvent mTempo = new TempoChangeEvent(500000, -1);

  public TempoDetector(Producer<RichMidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void registerConsumer(Consumer<TempoChangeEvent> xiConsumer)
  {
    // Immediately tell new consumers what the current tempo is.
    super.registerConsumerAndUpdate(xiConsumer, mTempo);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Discard events
  }
}
