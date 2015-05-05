package org.dodgyjammers.jammypiece.musickb;

public enum NoteLength
{
  SEMIQUAVER(16),
  QUAVER(8),
  CROTCHET(4),
  MINIM(2),
  SEMIBREVE(1);

  /**
   * Number of notes, of this length, per breve (or "whole note").
   */
  public final int mPerBreve;

  private NoteLength(int xiPerBreve)
  {
    mPerBreve = xiPerBreve;
  }
}
