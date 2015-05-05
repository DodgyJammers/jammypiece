package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Time signature detection component.
 *
 * Not at all clear that this component has all the inputs that it needs.  I'm expecting it to be a dummy component
 * that simply reports a configured value.
 */
public class TimeSignatureDetector extends Distributor<TimeSignatureChangeEvent> implements Consumer<MidiEvent>
{

  public TimeSignatureDetector(Producer<MidiEvent> xiMelodySource,
                               Producer<TempoChangeEvent> xiTempoSource)
  {
    xiMelodySource.registerConsumer(this);
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    // Discard melody events
  }
}
