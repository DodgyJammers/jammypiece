package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.TempoChangeEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class TempoDetector extends Distributor<TempoChangeEvent> implements Consumer<MidiEvent>
{
  private TempoChangeEvent mTempo = null;

  public TempoDetector(Producer<MidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void registerConsumer(Consumer<TempoChangeEvent> xiConsumer)
  {
    super.registerConsumer(xiConsumer);

    // Immediately tell new consumers what the current key is.
    xiConsumer.consume(mTempo);
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    // Discard events
  }
}
