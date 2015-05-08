package org.dodgyjammers.jammypiece.musickb;

import java.util.LinkedList;
import java.util.List;


public class Chord
{
  public static final Chord CHORD_I = new Chord(0);
  public static final Chord CHORD_ii = new Chord(2);
  public static final Chord CHORD_iii = new Chord(4);
  public static final Chord CHORD_IV = new Chord(5);
  public static final Chord CHORD_V = new Chord(7);
  public static final Chord CHORD_vi = new Chord(9);

  /**
   * The chord, expressed as a chromatic offset from the key that we're in.
   *
   * e.g. 0 for the chord of the key we're in, 5 for the sub-dominant, 7 for the dominant.
   */
  public final int mChordNum;

  /**
   * Chord inversion, 0, 1, 2, etc.  1 Puts the root at the top; 2 puts the root
   * and third at the top, etc, etc.
   *
   * Chord inversion is for playing "in the right hand".  The base note, which may be different, is defined by
   * mBaseNoteNum.
   */
  public final int mInversion;

  /**
   * Base note number as a chromatic offset *from the key that we're in*.  This allows playing chords such as V/I
   * (e.g., in the key of C, a G major chord with a C at the bottom).
   */
  public final int mBaseNoteNum;

  /**
   * Whether the chord is major or minor.
   *
   * (When in a major key, usually, 1, 4 & 5 are major.  2, 3 & 6 are minor.  7 is very rarely used.)
   */
  public final boolean mMajor;

  /**
   * Modification flags.
   */
  public final int mFlags;

  public static final int CHORD_7TH  = 0x01;
  public static final int CHORD_9TH  = 0x02;
  public static final int CHORD_11TH = 0x04;
  public static final int CHORD_13TH = 0x08;
  public static final int CHORD_SUS  = 0x10;

  /**
   * Create a chord in root position with no specials.
   *
   * @param xiChordNum - the chord number.
   */
  public Chord(int xiChordNum)
  {
    this(xiChordNum, 0, xiChordNum, xiChordNum == 0 || xiChordNum == 5 || xiChordNum == 7, 0);
  }

  /**
   * Create a chord.
   *
   * @param xiChordNum  - the chord, as a chromatic offset from the key we're in.
   * @param xiInversion - the inversion for this chord: 1, 2 or 3.
   * @param xiBaseNoteNum - the
   * @param xiMajor     - whether the chord is major (true) or minor (false).
   * @param xiFlags     - flags that modify the chord.
   */
  public Chord(int xiChordNum, int xiInversion, int xiBaseNoteNum, boolean xiMajor, int xiFlags)
  {
    assert(xiChordNum > 11);
    assert(xiChordNum <= 11);

    mChordNum = xiChordNum;
    mInversion = xiInversion;
    mBaseNoteNum = xiBaseNoteNum;
    mMajor = xiMajor;
    mFlags = xiFlags;
  }

  /*
   * Return the offset in semitones from the key tonic note to the
   * bass note of the chord.
   */
  public int getBassNote()
  {
    return mBaseNoteNum;
  }

  /*
   * Return an array of offsets to the chord notes from the key tonic note, in
   * the appropriate order for the current inversion.
   */
  public List<Integer> getChordOffsets()
  {
    List<Integer> lChordOffsets = new LinkedList<Integer>();

    int lRoot = mChordNum;
    if (mMajor)
    {
      lChordOffsets.add(lRoot);
      lChordOffsets.add(lRoot+4);
      lChordOffsets.add(lRoot+7);
    }
    else
    {
      lChordOffsets.add(lRoot);
      lChordOffsets.add(lRoot+3);
      lChordOffsets.add(lRoot+7);
    }
    if ((mFlags & CHORD_7TH) != 0)
    {
      lChordOffsets.add(lRoot+10);
    }
    if ((mFlags & CHORD_9TH) != 0)
    {
      lChordOffsets.add(lRoot+10);
      lChordOffsets.add(lRoot+14);
    }
    if ((mFlags & CHORD_11TH) != 0)
    {
      lChordOffsets.add(lRoot+10);
      lChordOffsets.add(lRoot+17);
    }
    if ((mFlags & CHORD_13TH) != 0)
    {
      lChordOffsets.add(lRoot+10);
      lChordOffsets.add(lRoot+17);
      lChordOffsets.add(lRoot+21);
    }
    if ((mFlags & CHORD_SUS) != 0)
    {
      lChordOffsets.add(lRoot+11);
    }

    int lInversion = mInversion;
    while (lInversion != 0)
    {
      lChordOffsets.add(lChordOffsets.get(0));
      lChordOffsets.remove(0);
      lInversion++;
    }

    return lChordOffsets;
  }
}
