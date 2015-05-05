package org.dodgyjammers.jammypiece.musickb;

/**
 * Musical key.
 */
public enum Key
{
  C_MAJOR(60, true),
  Cs_MAJOR(61, true),
  D_MAJOR(62, true),
  Eb_MAJOR(63, true),
  E_MAJOR(64, true),
  F_MAJOR(65, true),
  Fs_MAJOR(66, true),
  G_MAJOR(67, true),
  Ab_MAJOR(68, true),
  A_MAJOR(69, true),
  Bb_MAJOR(70, true),
  B_MAJOR(71, true),

  C_MINOR(60, false),
  Cs_MINOR(61, false),
  D_MINOR(62, false),
  Eb_MINOR(63, false),
  E_MINOR(64, false),
  F_MINOR(65, false),
  Fs_MINOR(66, false),
  G_MINOR(67, false),
  Ab_MINOR(68, false),
  A_MINOR(69, false),
  Bb_MINOR(70, false),
  B_MINOR(71, false);

  /**
   * MIDI note number for the tonic.
   */
  public final int mTonicNoteNum;

  /**
   * Whether this is a major key.
   */
  public final boolean mMajor;

  private Key(int xiTonicNoteNum, boolean xiMajor)
  {
    mTonicNoteNum = xiTonicNoteNum;
    mMajor = xiMajor;
  }
}
