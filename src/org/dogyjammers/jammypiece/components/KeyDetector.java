package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.KeyChange;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class KeyDetector extends Distributor<KeyChange> implements Consumer<MidiEvent>
{
  private KeyChange mKey = null;

  public KeyDetector(Producer<MidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void registerConsumer(Consumer<KeyChange> xiConsumer)
  {
    super.registerConsumer(xiConsumer);

    // Immediately tell new consumers what the current key is.
    xiConsumer.consume(mKey);
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    // Discard events
  }
}
