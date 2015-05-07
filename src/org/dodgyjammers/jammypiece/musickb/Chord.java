package org.dodgyjammers.jammypiece.musickb;

public class Chord
{
  /**
   * The chord.
   *
   * e.g. 1 for the chord of the key we're in, 4 for the sub-dominant, 5 for the dominant.
   */
  public final int mChordNum;

  /**
   * The base note, as a note number from the key we're in - NOT from the chord that's being played.
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
    this(xiChordNum, xiChordNum, xiChordNum == 1 || xiChordNum == 4 || xiChordNum == 5, 0);
    assert(xiChordNum != 7);
  }

  /**
   * Create a chord.
   *
   * @param xiChordNum  - the chord number.
   * @param xiInversion - the inversion.
   * @param xiMajor     - whether the chord is major (true) or minor (false).
   * @param xiFlags     - flags that modify the chord.
   */
  public Chord(int xiChordNum, int xiBaseNoteNum, boolean xiMajor, int xiFlags)
  {
    mChordNum = xiChordNum;
    mBaseNoteNum = xiBaseNoteNum;
    mMajor = xiMajor;
    mFlags = xiFlags;
  }
}
