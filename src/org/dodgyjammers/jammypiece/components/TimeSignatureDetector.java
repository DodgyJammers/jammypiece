package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.TimeSignature;

/**
 * Time signature detection component.
 *
 * Not at all clear that this component has all the inputs that it needs.  I'm expecting it to be a dummy component
 * that simply reports a configured value.
 */
public class TimeSignatureDetector extends Distributor<TimeSignatureChangeEvent> implements Consumer<RichMidiEvent>
{

  private TimeSignatureChangeEvent mTimeSigEvent;

  /**
   * Create a new time signature detector.
   *
   * @param xiMelodySource - the source of melody note.
   * @param xiTempoSource  - the source of tempo information.
   *
   * This dummy version just distributes the configured time signature.
   */
  public TimeSignatureDetector(Producer<RichMidiEvent> xiMelodySource,
                               Producer<TempoChangeEvent> xiTempoSource)
  {
    xiMelodySource.registerConsumer(this);

    // Get the initial time signature.
    String lTimeSig = MachineSpecificConfiguration.getCfgVal(CfgItem.TIME_SIGNATURE, "COMMON_TIME");
    mTimeSigEvent = new TimeSignatureChangeEvent(TimeSignature.valueOf(lTimeSig), -1);
  }

  @Override
  public void registerConsumer(Consumer<TimeSignatureChangeEvent> xiConsumer)
  {
    // Immediately tell new consumers what the current time signature is.
    super.registerConsumerAndUpdate(xiConsumer, mTimeSigEvent);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Discard melody events
  }
}
