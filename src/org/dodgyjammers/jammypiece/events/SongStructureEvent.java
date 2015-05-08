package org.dodgyjammers.jammypiece.events;

import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.Key;

/**
 * Current information about where we are in the song, and what is
 * likely to happen next.  This is produced at the start of each bar
 * by the ChordSelector, but can arrive at other times too.
 *
 * Any negative or NULL values should be ignored.
 */
public class SongStructureEvent extends Event
{
  public final Chord mCurrentChord;   // Active chord
  public final Chord mNextChord;        // Expected next chord
  public final int mClicksTilChord;     // How long until the chord change

  public final Key mCurrentKey;         // Active key
  public final Key mNextKey;            // Expected next key
  public final int mClicksTilKey;       // How long until the chord change

  public final int [] mStructure;       // Song structure, ie 1,1,2,1
  public final int mSection;            // Index into the above array

  public final int mSectionLength;      // Beats in the current section.
  public final int mClicksTilSection;   // Expected time remaining in the
                                        // current section.

  public SongStructureEvent(long xiTimestamp)
  {
    super(xiTimestamp);

    mCurrentChord = null;
    mNextChord = null;
    mClicksTilChord = -1;

    mCurrentKey = null;
    mNextKey = null;
    mClicksTilKey = -1;

    mStructure = new int [] {1};
    mSection = 0;

    mSectionLength = -1;
    mClicksTilSection = -1;
  }


}
