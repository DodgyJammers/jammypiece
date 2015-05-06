package org.dodgyjammers.jammypiece.components;

import java.util.List;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Component to select a source of MIDI events.
 */
public class InputSelector extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  public InputSelector(List<Producer<MidiEvent>> xiProducers)
  {
    assert(xiProducers.size() == 1); // For now, only support a single producer
    xiProducers.get(0).registerConsumer(this);
  }

  @Override
  public void consume(MidiEvent xiItem) throws Exception
  {
    // Just a pass-through component for now.
    distribute(xiItem);
  }
}
