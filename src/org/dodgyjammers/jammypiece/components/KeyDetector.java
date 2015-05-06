package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Key;

public class KeyDetector extends Distributor<KeyChangeEvent> implements Consumer<MidiEvent>
{
  private KeyChangeEvent mKeyChangeEvent;

  public KeyDetector(Producer<MidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);

    // Get the initial key.
    String lKey = MachineSpecificConfiguration.getCfgVal(CfgItem.KEY_SIGNATURE, "C_MAJOR");
    mKeyChangeEvent = new KeyChangeEvent(Key.valueOf(lKey), -1);
  }

  @Override
  public void registerConsumer(Consumer<KeyChangeEvent> xiConsumer)
  {
    // Immediately tell new consumers what the current key is.
    super.registerConsumerAndUpdate(xiConsumer, mKeyChangeEvent);
  }

  @Override
  public void consume(MidiEvent xiItem) throws Exception
  {
    // Discard events
  }
}
