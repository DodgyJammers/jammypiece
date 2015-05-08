package org.dodgyjammers.jammypiece.components;

import java.util.List;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.Key;

public class Harmoniser extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;
  private final KeyChangeListener mKeyChangeListener;

  private Chord mCurrentChord;
  private Chord mNewChord;

  private int mHarmonyChannel;
  private int mBassChannel;

  private volatile Key mKey;

  /**
   * Produces a harmony based on the chord selected and the soloist's MIDI
   * events.
   * @param xiMelodySource
   * @param xiChordSource
   * @param xiKeyDetector
   * @param xiMetronome
   */
  public Harmoniser(Producer<RichMidiEvent> xiMelodySource,
                    Producer<ChordChangeEvent> xiChordSource,
                    Producer<KeyChangeEvent> xiKeyDetector,
                    Producer<TickEvent> xiMetronome)
  {
    mChordListener = new ChordListener();
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();
    mHarmonyChannel = MachineSpecificConfiguration.getCfgVal(CfgItem.CHORD_CHANNEL, 0);
    mBassChannel = MachineSpecificConfiguration.getCfgVal(CfgItem.BASS_CHANNEL, 0);

    xiMelodySource.registerConsumer(this);
    xiChordSource.registerConsumer(mChordListener);
    xiKeyDetector.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);
  }

  @Override
  public void consume(RichMidiEvent xiItem)
  {
    // Discard melody events, later we will add more smarts based on the
    // current midi event.
  }

  private class ChordListener implements Consumer<ChordChangeEvent>
  {
    @Override
    public void consume(ChordChangeEvent xiItem)
    {
      mNewChord = xiItem.mChord;
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

	    if (xiItem.mStress)
	    {
	      playNewChord();
	    }
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
      //iterate over the notes in the chord and stop them all.
      for (int note: getNotes(mCurrentChord))
      {
        distribute(RichMidiEvent.makeNoteOff(mHarmonyChannel, note));
      }
    }

	  //iterate over the notes in the new chord and play them all (for now at once
    // and at the same noise.
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
    int lBassNote = xiChord.getBassNote() + mKey.mTonicNoteNum;
    return lBassNote;
  }
}
