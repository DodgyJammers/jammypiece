package org.dodgyjammers.jammypiece.events;

import java.util.Arrays;

import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.Key;

/**
 * Chord change.
 */
public class ChordChangeEvent
{
  public final Chord mChord;            // Active chord

  public final Chord mNextChord;        // Expected next chord
  public final int mClicksTilChord;     // How long until the chord change

  public final Key mCurrentKey;         // Active key
  public final Key mNextKey;            // Expected next key
  public final int mClicksTilKey;       // How long until the chord change

  public final int [] mStructure;       // Song structure, ie 1,1,2,1
  public final int mSection;            // Index into the above array

  public final int mSectionLength;      // Beats (i.e., ticks with tickInBeat==0) in the current section.
  public final int mClicksTilSection;   // Expected time remaining in the
                                        // current section.


  public ChordChangeEvent(Chord xiChord)
  {
    this(xiChord, null, -1, null, null, -1, new int [] {1}, 0, -1, -1);

  }

  public ChordChangeEvent(Chord xiChord,
                          Chord xiNextChord,
                          int xiClicksTilChord,
                          Key xiCurrentKey,
                          Key xiNextKey,
                          int xiClicksTilKey,
                          int [] xiStructure,
                          int xiSection,
                          int xiSectionLength,
                          int xiClicksTilSection)
  {
    mChord = xiChord;
    mNextChord = xiNextChord;
    mClicksTilChord = xiClicksTilChord;

    mCurrentKey = xiCurrentKey;
    mNextKey = xiNextKey;
    mClicksTilKey = xiClicksTilKey;

    mStructure = xiStructure;
    mSection = xiSection;

    mSectionLength = xiSectionLength;
    mClicksTilSection = xiClicksTilSection;
  }
  
  @Override
  public String toString() {
    return mChord + "(->" + mClicksTilChord + ".." + mNextChord + ") in " + 
           mCurrentKey + "(->" + mClicksTilKey + ".." + mNextKey + ") at " +
           Arrays.toString(mStructure) + "(" + mSection + ") " + mSectionLength + "(.." + mClicksTilSection + ")";
  }
}

