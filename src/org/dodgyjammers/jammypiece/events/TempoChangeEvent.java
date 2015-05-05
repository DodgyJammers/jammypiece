package org.dodgyjammers.jammypiece.events;

/**
 * Tempo change.
 */
public class TempoChangeEvent extends Event
{
  /**
   * Tempo, in microseconds per crotchet (which is the standard MIDI tempo unit).
   *
   * Be careful in 6/8 time!
   */
  public final long mTempo;

  /**
   * Create a tempo-change event.
   *
   * @param xiTempo     - the new tempo, in microseconds per crotchet.
   * @param xiTimestamp - time at which the event takes place.
   */
  public TempoChangeEvent(long xiTempo, long xiTimestamp)
  {
    super(xiTimestamp);
    mTempo = xiTempo;
  }
}
