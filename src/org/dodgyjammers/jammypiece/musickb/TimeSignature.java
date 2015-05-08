package org.dodgyjammers.jammypiece.musickb;

/**
 * Time signature.
 */
public enum TimeSignature
{
  COMMON_TIME(4, NoteLength.CROTCHET, "-...",   4),
  CUT_TIME   (2, NoteLength.MINIM,    "-.",     4),
  WALTZ      (3, NoteLength.CROTCHET, "-..",    4),
  SIX_EIGHT  (6, NoteLength.QUAVER,   "-..-..", 6);

  public final int mNumBeats;
  public final NoteLength mNoteLength;
  public final String mStressPattern;
  public final int mTicksPerBeat;

  private TimeSignature(int xiNumBeats, NoteLength xiNoteLength, String xiStressPattern, int xiTicksPerBeat)
  {
    assert(xiNumBeats == xiStressPattern.length()) : "Wrong number of beats in stress pattern";
    mNumBeats = xiNumBeats;
    mNoteLength = xiNoteLength;
    mStressPattern = xiStressPattern;
    mTicksPerBeat = xiTicksPerBeat;
  }
}
