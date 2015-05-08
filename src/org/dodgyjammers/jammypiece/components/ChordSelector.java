package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.ChordMap;
import org.dodgyjammers.jammypiece.musickb.Key;

public class ChordSelector extends Distributor<ChordChangeEvent> implements Consumer<RichMidiEvent>
{
  private final KeyChangeListener mKeyChangeListener;
  private final MetronomeListener mMetronomeListener;
  private final SongStructure mSongStructure;
  private final ChordMap mChordMap;

  private volatile Key mKey = Key.valueOf("C_MAJOR");

  private volatile boolean mPlayTonicNext = true;

  public ChordSelector(Producer<RichMidiEvent> xiMelodySource,
                       Producer<KeyChangeEvent> xiKeySource,
                       Producer<TickEvent> xiMetronome)
  {
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();
    mChordMap = new ChordMap();
    mSongStructure = new SongStructure();

    xiMelodySource.registerConsumer(this);
    xiKeySource.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Receive and discard melody events.
  }

  private class SongStructure extends Distributor<ChordChangeEvent>
  {
    public int [] mStructure;       // Song structure, ie 1,1,2,1
    public int mSection;            // Index into the above array

    public int mSectionLength;      // Beats in the current section.
    public int mBeatsLeftInSection; // Beats left in this section.



    public void SongStructure()
    {
      mStructure = new int [] {1, 1, 2, 1};
      mSection = 0;
      mSectionLength = 16;
      mBeatsLeftInSection = 16;
    }

    public void produceChordChangeEvent(Chord xiChord)
    {
      distribute(new ChordChangeEvent(xiChord));
    }

    /*
     * Are we moving away from or towards the root chord?
     * -1 for towards, 1 for away, 0 for no preference.
     */
    public int direction()
    {
      return 0;
    }

    /*
     * For a chord selected now, how far from the tonic are we allowed to be?
     * Zero means that the tonic chord is the only answer!
     */
    public int max_dist()
    {
      return 0;
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiTick) throws Exception
    {
      // For now, amuse Chris by changing the chord each bar.
      if (xiTick.mStress)
      {
        // Pass a chord to the harmoniser
        if (mPlayTonicNext)
        {
          distribute(new ChordChangeEvent(Chord.CHORD_I));
          mPlayTonicNext = false;
        }
        else
        {
          distribute(new ChordChangeEvent(Chord.CHORD_V));
          mPlayTonicNext = true;
        }
      }
    }
  }

  private class KeyChangeListener implements Consumer<KeyChangeEvent>
  {
    @Override
    public void consume(KeyChangeEvent xiKeyEvent) throws Exception
    {
      // Store key change events.
      mKey = xiKeyEvent.mNewKey;
    }
  }
}

