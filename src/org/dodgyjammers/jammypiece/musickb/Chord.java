package org.dodgyjammers.jammypiece.musickb;

import java.util.LinkedList;
import java.util.List;


public class Chord
{
  public static final Chord CHORD_I = new Chord(1);
  public static final Chord CHORD_ii = new Chord(2);
  public static final Chord CHORD_iii = new Chord(3);
  public static final Chord CHORD_IV = new Chord(4);
  public static final Chord CHORD_V = new Chord(5);
  public static final Chord CHORD_vi = new Chord(6);

  /**
   * The chord.
   *
   * e.g. 1 for the chord of the key we're in, 4 for the sub-dominant, 5 for the dominant.
   */
  public final int mChordNum;

  /**
   * Chord inversion, 0, 1, 2, etc.  1 Puts the root at the top; 2 puts the root
   * and third at the top, etc, etc.
   */
  public final int mInversion;

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
    this(xiChordNum, 0, xiChordNum == 1 || xiChordNum == 4 || xiChordNum == 5, 0);
    assert(xiChordNum != 7);
  }

  /**
   * Create a chord.
   *
   * @param xiChordNum  - the chord number.
   * @param xiInversion - the inversion for this chord: 1, 2 or 3.
   * @param xiMajor     - whether the chord is major (true) or minor (false).
   * @param xiFlags     - flags that modify the chord.
   */
  public Chord(int xiChordNum, int xiInversion, boolean xiMajor, int xiFlags)
  {
    mChordNum = xiChordNum;
    mInversion = xiInversion;
    mMajor = xiMajor;
    mFlags = xiFlags;
  }

  public int getRootOffset()
  {
    switch (mChordNum)
    {
      case 1: return 0;
      case 2: return 2;
      case 3: return 4;
      case 4: return 5;
      case 5: return 7;
      case 6: return 9;
    }

    throw new RuntimeException("Oops");
  }

  /*
   * Return the offset in semitones from the key tonic note to the
   * bass note of the chord.
   */
  public int getBassNote()
  {
    List<Integer> lChordOffsets = getChordOffsets();
    return lChordOffsets.get(0);
  }

  /*
   * Return an array of offsets to the chord notes from the key tonic note, in
   * the appropriate order for the current inversion.
   */
  public List<Integer> getChordOffsets()
  {
    List<Integer> lChordOffsets = new LinkedList<Integer>();

    int lRoot = getRootOffset();
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
