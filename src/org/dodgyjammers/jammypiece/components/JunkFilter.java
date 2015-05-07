package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.ShortMessage;

import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class JunkFilter extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  public JunkFilter(Producer<RichMidiEvent> xiMelodySource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    if (xiItem.getMessage().getStatus() != ShortMessage.TIMING_CLOCK)
    {
      distribute(xiItem);
    }
  }
}
