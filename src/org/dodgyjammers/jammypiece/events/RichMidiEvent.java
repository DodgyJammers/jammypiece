package org.dodgyjammers.jammypiece.events;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Add some extra helpers to the MidiEvent.
 *
 */
public class RichMidiEvent extends MidiEvent
{
  private final MidiMessage mMessage;
  private final long mTick;
  
  private final int mStatus;
  private final int mNote;
  private final int mVelocity;
  
  public RichMidiEvent(MidiEvent xiEvent) {
    this(xiEvent.getMessage(), xiEvent.getTick());
  }
  
  public RichMidiEvent(MidiMessage xiMessage, long xiTick)
  {
    super(xiMessage, xiTick);
    mMessage = xiMessage;
    mTick = xiTick;
    mStatus = mMessage.getStatus();
    if ((mStatus & 0xE0) == 0x80) {
      mNote = mMessage.getMessage()[1] & 0xFF;
      mVelocity = mMessage.getMessage()[2] & 0xFF;
    } else {
      mNote = mVelocity = -1;
    }
  }
  
  public static RichMidiEvent makeNoteOff(int xiChan, int xiNoteVal)
  {
    try {
      return new RichMidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, xiChan, xiNoteVal, 0), -1);
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  public static RichMidiEvent makeNoteOn(int xiChan, int xiNoteVal)
  {
    return makeNoteOn(xiChan, xiNoteVal, 40);
  }

  public static RichMidiEvent makeNoteOn(int xiChan, int xiNoteVal, int xiVelocity)
  {
    try {
      return new RichMidiEvent(new ShortMessage(ShortMessage.NOTE_ON, xiChan, xiNoteVal, xiVelocity), -1);
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Is this a note message?
   */
  public boolean isNoteOnOff() {
    return mNote != -1;
  }

  public int getNote() {
    return mNote;
  }
  
  public int getVelocity() {
    return mVelocity;
  }

  /**
   * Same event, but with a different note value. Only call this if it's a note
   * on/off message!
   */
  public RichMidiEvent withNote(int xiNoteVal) {
    try
    {
      return new RichMidiEvent(new ShortMessage(mStatus, xiNoteVal, mVelocity), mTick);
    }
    catch (InvalidMidiDataException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public RichMidiEvent at(long xiTick)
  {
    return new RichMidiEvent(getMessage(), xiTick);
  }
}
