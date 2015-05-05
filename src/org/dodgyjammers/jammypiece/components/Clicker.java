package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Clicker component, converts internal metronome ticks into audible events.
 */
public class Clicker extends Distributor<MidiEvent> implements Consumer<TickEvent>
{

  public Clicker(Producer<TickEvent> xiMetronome)
  {
    xiMetronome.registerConsumer(this);
  }

  @Override
  public void consume(TickEvent xiTick)
  {
    // Discard ticks
  }
}
