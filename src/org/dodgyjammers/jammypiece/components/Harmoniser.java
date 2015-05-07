package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.Key;

public class Harmoniser extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;
  private final KeyChangeListener mKeyChangeListener;
  
  private Chord mCurrentChord;
  private int mHarmonyChannel;
  private Chord mNewChord;
  
  private volatile Key mKey;

  public Harmoniser(Producer<RichMidiEvent> xiMelodySource,
                    Producer<ChordChangeEvent> xiChordSource,
                    Producer<KeyChangeEvent> xiKeyDetector,
                    Producer<TickEvent> xiMetronome)
  {
    mChordListener = new ChordListener();
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();

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
      mNewChord = getChord(xiItem);
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiItem)
    {
	  playNewChord();
    }
  }

  public void playNewChord() 
  {
	//iterate over the notes in the chord and stop them all.
    /*
    for (int note: mCurrentChord.getnotes())
    {
      distribute(RichMidiEvent.makeNoteOff(mHarmonyChannel, note));
    }
	
	//iterate over the notes in the new chord and play them all (for now at once
    // and at the same noise.
    for(int note: mNewChord.getNotes())
    {
	  distribute(RichMidiEvent.makeNoteOn(mHarmonyChannel, note));
    }
    */
    
	mCurrentChord = mNewChord;
  }

  public Chord getChord(ChordChangeEvent xiItem) 
  {
	Chord lChord = null;
	//xiItem.getChord();
	// Pull the chord being set from the chord change event.
	return lChord;
  }
 
  private class KeyChangeListener implements Consumer<KeyChangeEvent>
  {
    @Override
    public void consume(KeyChangeEvent xiKey) throws Exception
    {
      mKey = xiKey.mNewKey;
    }
  }
}
