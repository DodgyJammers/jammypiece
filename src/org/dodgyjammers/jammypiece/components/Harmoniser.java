package org.dodgyjammers.jammypiece.components;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.Key;
import org.dodgyjammers.jammypiece.musickb.TimeSignature;

public class Harmoniser extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;
  private final KeyChangeListener mKeyChangeListener;
  private final TimeSignatureListener mTimeSigListener;

  private TimeSignature mTimeSignature;

  private Chord mCurrentChord;
  private Chord mNewChord;

  private int mHarmonyChannel;
  private int mBassChannel;
  private int mNumBeats;
  private int mArpeggNum = 0;
  private int mSection;

  private boolean mDuet = false;

  private String[] mHarmonyStyle = new String[2];
  private int[] mStructure;

  private volatile Key mKey;

  /**
   * Produces a harmony based on the chord selected and the soloist's MIDI
   * events.
   * @param xiMelodySource
   * @param xiChordSource
   * @param xiKeyDetector
   * @param xiMetronome
   * @param xiTimeSigDetector
   */
  public Harmoniser(Producer<RichMidiEvent> xiMelodySource,
                    Producer<ChordChangeEvent> xiChordSource,
                    Producer<KeyChangeEvent> xiKeyDetector,
                    Producer<TickEvent> xiMetronome,
                    Producer<TimeSignatureChangeEvent> xiTimeSigDetector)
  {
    mChordListener = new ChordListener();
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();
    mTimeSigListener = new TimeSignatureListener();
    mHarmonyChannel = MachineSpecificConfiguration.getCfgVal(CfgItem.CHORD_CHANNEL, 0);
    mBassChannel = MachineSpecificConfiguration.getCfgVal(CfgItem.BASS_CHANNEL, 0);
    mHarmonyStyle[0] = MachineSpecificConfiguration.getCfgVal(CfgItem.HARMONY_STYLE_A, "ARPEGGIO");
    mHarmonyStyle[1] = MachineSpecificConfiguration.getCfgVal(CfgItem.HARMONY_STYLE_B, "CHORDS");

    xiMelodySource.registerConsumer(this);
    xiChordSource.registerConsumer(mChordListener);
    xiKeyDetector.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);
    xiTimeSigDetector.registerConsumer(mTimeSigListener);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws InvalidMidiDataException
  {
    if (!xiItem.isNoteOnOff())
    {
      // Discard events other than note on/off.
      return;
    }

    // From the current chord and the received event, find the closest duet note, below the melody.
    if (mDuet)
    {
      int lBestOffsetFromNote = 0;

      int lNote = xiItem.getNote() % 12;
      for (int lOffsetFromKey : mCurrentChord.getChordOffsets())
      {
        int lChordNote = (mKey.mTonicNoteNum + lOffsetFromKey) % 12;
        int lOffsetFromNote = (lNote - lChordNote) % 12;
        while (lOffsetFromNote < 1)
        {
          lOffsetFromNote += 12;
        }
        if (lOffsetFromNote > lBestOffsetFromNote)
        {
          lBestOffsetFromNote = lOffsetFromNote;
        }
      }

      // Create a matching event.
      byte[] lMessageBytes = xiItem.getMessage().getMessage();
      lMessageBytes[1] += (lBestOffsetFromNote - 12);
      MidiEvent lEvent = new MidiEvent(new ShortMessage(lMessageBytes[0], lMessageBytes[1], lMessageBytes[2]),
                                       xiItem.getTick());
      distribute(new RichMidiEvent(lEvent));
    }
  }

  private class ChordListener implements Consumer<ChordChangeEvent>
  {

    @Override
    public void consume(ChordChangeEvent xiItem)
    {
      mNewChord = xiItem.mChord;
      mSection = xiItem.mSection;
      mStructure = xiItem.mStructure;
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiItem)
    {
      if(xiItem.mTickInBeat == 0)
      {
        playNewBassNote();
      }

      playHarmony(mSection, xiItem);

    }
  }

  public void playNewBassNote()
  {
    if (mCurrentChord != null)
    {
      int lCurrentBassNote = getPlayableBass(mCurrentChord);
      distribute(RichMidiEvent.makeNoteOff(mBassChannel, lCurrentBassNote));
    }

    int lNewBassNote = getPlayableBass(mNewChord);
    distribute(RichMidiEvent.makeNoteOn(mBassChannel, lNewBassNote));
  }

  public void playNewChord()
  {
    if (mCurrentChord != null)
    {
      stopChord(mCurrentChord, mHarmonyChannel);
    }

	  //iterate over the notes in the new chord and play them all (for now at once
    // and at the same noise.
    //
    // Play some variation of the new harmony
    for(int note: getNotes(mNewChord))
    {
	    distribute(RichMidiEvent.makeNoteOn(mHarmonyChannel, note));
    }

	  mCurrentChord = mNewChord;
  }

  private int[] getNotes(Chord xiChord)
  {
    List<Integer> lNoteOffsets = xiChord.getChordOffsets();
    int[] lNotes = new int[lNoteOffsets.size()];
    int iterator = 0;
    for (int lOffset : lNoteOffsets)
    {
      lNotes[iterator] = mKey.mTonicNoteNum + lOffset;
      iterator++;
    }
    return lNotes;
  }

  private class KeyChangeListener implements Consumer<KeyChangeEvent>
  {
    @Override
    public void consume(KeyChangeEvent xiKey) throws Exception
    {
      mKey = xiKey.mNewKey;
    }
  }

  private int getPlayableBass(Chord xiChord)
  {
    int lBassNote = xiChord.getBassNote() + mKey.mTonicNoteNum - 24;
    return lBassNote;
  }

  private void arpeggiation()
  {
    if (mArpeggNum < mNumBeats)
    {
      if (mCurrentChord != null)
      {
        stopChord(mCurrentChord, mHarmonyChannel);
      }
      distribute(RichMidiEvent.makeNoteOn(mHarmonyChannel, getNotes(mNewChord)[mArpeggNum]));
      mArpeggNum = mArpeggNum + 1;
    }
    else
    {
      mArpeggNum = 0;
      arpeggiation();
    }

  }

  private class TimeSignatureListener implements Consumer<TimeSignatureChangeEvent>
  {
    @SuppressWarnings("synthetic-access")
    @Override
    public void consume(TimeSignatureChangeEvent xiEvent) throws Exception
    {
      mTimeSignature = xiEvent.mTimeSignature;
      mNumBeats = mTimeSignature.mNumBeats;
    }
  }

  private void stopChord(Chord xiChord,int xiChannel)
  {
    //iterate over the notes in the chord and stop them all.
    for (int note: getNotes(xiChord))
    {
      distribute(RichMidiEvent.makeNoteOff(xiChannel, note));
    }
  }

  private void playHarmony(int xiSection, TickEvent xiItem)
  {
    String lStyle = mHarmonyStyle[mStructure[mSection] - 1];

    mDuet = false;

    switch(lStyle)
    {
      case "CHORDS":
        if(xiItem.mStress)
        {
          playNewChord();
        }
        break;
      case "ARPEGGIO":
        if(xiItem.mTickInBeat == 0)
        {
          arpeggiation();
        }
        break;
      case "DUET":
        mDuet = true;
        break;
    }
  }
}
