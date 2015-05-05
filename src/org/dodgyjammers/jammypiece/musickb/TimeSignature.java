package org.dodgyjammers.jammypiece.musickb;

/**
 * Time signature.
 */
public enum TimeSignature
{
  COMMON_TIME(4, NoteLength.CROTCHET, "-..."  ),
  CUT_TIME   (2, NoteLength.MINIM,    "-."    ),
  WALTZ      (3, NoteLength.CROTCHET, "-.."   ),
  SIX_EIGHT  (6, NoteLength.QUAVER,   "-..-..");

  public final int mNumBeats;
  public final NoteLength mNoteLength;
  public final String mStressPattern;

  private TimeSignature(int xiNumBeats, NoteLength xiNoteLength, String xiStressPattern)
  {
    mNumBeats = xiNumBeats;
    mNoteLength = xiNoteLength;
    mStressPattern = xiStressPattern;
  }
}
