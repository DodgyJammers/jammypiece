package org.dodgyjammers.jammypiece.components;

import java.util.List;

import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Component to select a source of MIDI events.
 */
public class InputSelector extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  public InputSelector(List<Producer<RichMidiEvent>> xiProducers)
  {
    assert(xiProducers.size() <= 1); // For now, only support a single producer
    if (xiProducers.size() == 1) {
      xiProducers.get(0).registerConsumer(this);
    }
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Just a pass-through component for now.
    distribute(xiItem);
  }
}
