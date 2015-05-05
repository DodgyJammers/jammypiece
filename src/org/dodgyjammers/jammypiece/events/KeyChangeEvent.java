package org.dodgyjammers.jammypiece.events;

import org.dodgyjammers.jammypiece.musickb.Key;

/**
 * Key change.
 */
public class KeyChangeEvent extends Event
{
  /**
   * The new key signature.
   */
  public final Key mNewKey;

  /**
   * Create a key-change event.
   *
   * @param xiKey       - new key signature.
   * @param xiTimestamp - time at which the event takes place.
   */
  public KeyChangeEvent(Key xiKey, long xiTimestamp)
  {
    super(xiTimestamp);
    mNewKey = xiKey;
  }
}
