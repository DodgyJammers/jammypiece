package org.dodgyjammers.jammypiece.events;

import org.dodgyjammers.jammypiece.musickb.Chord;

/**
 * Chord change.
 */
public class ChordChangeEvent
{
  public final Chord mChord;

  public ChordChangeEvent(Chord xiChord)
  {
    mChord = xiChord;
  }
}
