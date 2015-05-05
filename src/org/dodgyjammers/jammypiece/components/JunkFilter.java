package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;

public class JunkFilter extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  @Override
  public void consume(MidiEvent xiItem) {
    if (xiItem.getMessage().getStatus() != 0xF8) // Timing Clock
    {
      distribute(xiItem);
    }
  }
}
