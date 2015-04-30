package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

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
