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
    // Immediately tell new consumers what the current key is.
    super.registerConsumerAndUpdate(xiConsumer, mKey);
  }

  @Override
  public void consume(MidiEvent xiItem) throws Exception
  {
    // Discard events
  }
}
