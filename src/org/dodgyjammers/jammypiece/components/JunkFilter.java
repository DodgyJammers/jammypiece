package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class JunkFilter extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  public JunkFilter(Producer<MidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void consume(MidiEvent xiItem) throws Exception
  {
    if (xiItem.getMessage().getStatus() != ShortMessage.TIMING_CLOCK)
    {
      distribute(xiItem);
    }
  }
}
