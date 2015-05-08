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

  /*
   * Number of ticks per beat.
   */
  public final int mTicksPerBeat;

  /*
   * Which tick this is - 0 is on the beat.
   */
  public final int mTickInBeat;

  /**
   * Create a tick event.
   *
   * @param xiStress    - whether this tick event occurs on a stressed beat.
   * @param xiTimestamp - time at which the event takes place.
   */
  public TickEvent(boolean xiStress, int xiTicksPerBeat, int xiTickInBeat, long xiTimestamp)
  {
    super(xiTimestamp);
    mStress = xiStress;
    mTicksPerBeat = xiTicksPerBeat;
    mTickInBeat = xiTickInBeat;
  }
}
