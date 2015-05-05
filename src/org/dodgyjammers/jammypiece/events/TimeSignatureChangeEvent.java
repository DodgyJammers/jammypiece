package org.dodgyjammers.jammypiece.events;

import org.dodgyjammers.jammypiece.musickb.TimeSignature;

/**
 * Time signature change.
 */
public class TimeSignatureChangeEvent extends Event
{
  /**
   * The new time signature.
   */
  public final TimeSignature mTimeSignature;

  /**
   * Create a time signature-change event.
   *
   * @param xiTimeSignature - the new time signature.
   * @param xiTimestamp     - time at which the event takes place.
   */
  public TimeSignatureChangeEvent(TimeSignature xiTimeSignature, long xiTimestamp)
  {
    super(xiTimestamp);
    mTimeSignature = xiTimeSignature;
  }
}
