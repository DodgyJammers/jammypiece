package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class KeyDetector extends Distributor<KeyChangeEvent> implements Consumer<MidiEvent>
{
  private KeyChangeEvent mKey = null;

  public KeyDetector(Producer<MidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void registerConsumer(Consumer<KeyChangeEvent> xiConsumer)
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
