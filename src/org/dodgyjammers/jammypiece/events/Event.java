package org.dodgyjammers.jammypiece.events;

/**
 * Abstract timed event class.  Superclass of all events that can be transferred between components.
 */
public abstract class Event
{
  /**
   * The time at which the change takes effect.
   *
   * !! ARR Units?  Should be the same as on MIDI events which I think is microseconds since device opened.
   * Not so - it's "ticks" isn't it? So depends on the MPQ (milliseconds-per-crotchet) setting.
   */
  public final long mTimestamp;

  /**
   * Create a new event.
   */
  protected Event(long xiTimestamp)
  {
    mTimestamp = xiTimestamp;
  }
}