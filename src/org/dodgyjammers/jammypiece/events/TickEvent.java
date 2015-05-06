package org.dodgyjammers.jammypiece.events;

/**
 * Metronome tick.
 */
public class TickEvent extends Event
{
  /**
   * Whether this tick event occurs on a stressed beat.
   */
  public final boolean mStress;

  /**
   * Create a tick event.
   *
   * @param xiStress    - whether this tick event occurs on a stressed beat.
   * @param xiTimestamp - time at which the event takes place.
   */
  public TickEvent(boolean xiStress, long xiTimestamp)
  {
    super(xiTimestamp);
    mStress = xiStress;
  }
}
