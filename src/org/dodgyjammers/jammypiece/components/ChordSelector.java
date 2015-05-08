package org.dodgyjammers.jammypiece.components;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.Chord;
import org.dodgyjammers.jammypiece.musickb.ChordMap;
import org.dodgyjammers.jammypiece.musickb.ChordMap.ChordGroup;
import org.dodgyjammers.jammypiece.musickb.Key;

public class ChordSelector extends Distributor<ChordChangeEvent> implements Consumer<RichMidiEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final KeyChangeListener mKeyChangeListener;
  private final MetronomeListener mMetronomeListener;
  private final SongStructure mSongStructure;
  private final ChordMap mChordMap;

  private volatile Key mKey = Key.valueOf("C_MAJOR");
  private volatile Chord mChord;
  private volatile ChordGroup mChordGroup;
  private volatile int mCurrentNote = 0;  // Pitch of current note - 0 means silence.

  public ChordSelector(Producer<RichMidiEvent> xiMelodySource,
                       Producer<KeyChangeEvent> xiKeySource,
                       Producer<TickEvent> xiMetronome)
  {
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();
    mChordMap = new ChordMap();
    mChord = null;
    mChordGroup = null;
    mSongStructure = new SongStructure();

    xiMelodySource.registerConsumer(this);
    xiKeySource.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Keep track of the current solo note.
    if (xiItem.isNoteOnOff())
    {
      if  (xiItem.getVelocity() != 0)
      {
        // Note on
        mCurrentNote = xiItem.getNote();
      }
      else
      {
        // Note off
        mCurrentNote = 0;
      }
    }
  }

  /*
   * Optionally, select a new chord, and update mChord and
   * mChordGroup appropriately.  Return true if anything changes,
   * false otherwise.
   */
  boolean getNewChord(int lDirn, int lDist)
  {
    boolean lChanged = false;

    ArrayList<ChordGroup> lCandidates = new ArrayList();
    if (mChordGroup == null)
    {
      mChordGroup = mChordMap.getRoot();
      lChanged = true;
    }
    for (ChordGroup lNeighbour: mChordGroup.mNeighbours)
    {
      // Abort if the neighbour is in the wrong direction
      if ((lDirn == 0) &&
          (lNeighbour.mShortestPathHome != mChordGroup.mShortestPathHome))
        continue;
      if ((lDirn == 1) &&
          (lNeighbour.mShortestPathHome <= mChordGroup.mShortestPathHome))
        continue;
      if ((lDirn == -1) &&
          (lNeighbour.mShortestPathHome >= mChordGroup.mShortestPathHome))
        continue;

      // Abort if the neighbour is too far away.
      if (lNeighbour.mShortestPathHome > lDist)
        continue;
    }

    // Loop through the shuffled chord groups and variations, looking
    // for one that is compatible.
    int lNote = 0;
    if (mCurrentNote != 0)
    {
      lNote = mCurrentNote - mKey.mTonicNoteNum;
      while (lNote <= 0)
        lNote += 12;
    }
    if (lCandidates.size() != 0)
    {
      Collections.shuffle(lCandidates);
      for (ChordGroup lGroup: lCandidates)
      {
        ArrayList<Chord> lChords = new ArrayList<Chord>();
        lChords.addAll(lGroup.mVariations);
        Collections.shuffle(lChords);
        for (Chord lChord: lChords)
        {
          // Move along if this note clashes
          if ((mCurrentNote != 0) && lChord.clashes(lNote))
          {
            continue;
          }

          // Select this Chord
          mChordGroup = lGroup;
          mChord = lChord;
          lChanged = true;
          break;
        }
        if (lChanged)
          break;
      }
    }

    return lChanged;
  }


  private void consumeMetronome(TickEvent xiTick)
  {
    // Ignore anything shorter than a beat.
    if (xiTick.mTickInBeat != 0)
    {
      return;
    }

    // Update the song position.
    mSongStructure.consumeMetronome(xiTick);

    // Work out the chord we should be playing now.  We need several
    // inputs for this.
    int lDirn = mSongStructure.getDirection();
    int lDist = mSongStructure.getMaxDist();
    boolean lchanged = getNewChord(lDirn, lDist);

    if ((lchanged) || (xiTick.mStress))
    {
      // @@@ It would be nice to start predicting the next chord.
      LOGGER.info("Distributing chord change: " + mChord);
      mSongStructure.produceChordChangeEvent(mChord,
                                             mChord,
                                             xiTick.mTickInBeat);
    }
  }

  private class SongStructure
  {
    public int [] mStructure;       // Song structure, ie 1,1,2,1
    public int mSection;            // Index into the above array

    public int mSectionLength;      // Beats in the current section.
    public int mBeatsLeftInSection; // Beats left in this section.
    public int mClicksPerBeat;


    public SongStructure()
    {
      // We'll be consuming a metronome before being used for the first time,
      // so initialise as if as the end of a piece.
      mStructure = new int [] {1, 1, 2, 1};
      mSection = 4;
      mSectionLength = 16;
      mBeatsLeftInSection = 1;
      mClicksPerBeat = 3;
    }

    public void consumeMetronome(TickEvent xiTick)
    {
      // For now, use the main beats to navigate through
      // a 16-bar blues structure.
      if (xiTick.mTickInBeat == 0)
      {
        mBeatsLeftInSection--;
        mClicksPerBeat = xiTick.mTicksPerBeat;
        if (mBeatsLeftInSection == 0)
        {
          mBeatsLeftInSection = mSectionLength;
          mSection++;
          switch (mSection)
          {
            case 2:
              mKey = Key.valueOf("G_MAJOR");
              break;
            case 3:
              mKey = Key.valueOf("C_MAJOR");
              break;
            case 4:
              mSection = 0;
              break;
            default:
              break;
          }
        }
      }
    }

    public void produceChordChangeEvent(Chord xiChord,
                                        Chord xiNextChord,
                                        int xiClicksTilChord)
    {
      Key lNextKey = (mSection == 3)? Key.valueOf("C_MAJOR"): Key.valueOf("G_MAJOR");

      distribute(new ChordChangeEvent(xiChord,
                                      xiNextChord,
                                      xiClicksTilChord,
                                      mKey,
                                      lNextKey,
                                      mBeatsLeftInSection * mClicksPerBeat,
                                      mStructure,
                                      mSection,
                                      mSectionLength,
                                      mBeatsLeftInSection * mClicksPerBeat));
    }

    /*
     * Are we moving away from or towards the root chord?
     * -1 for towards, 1 for away, 0 for no preference.
     */
    public int getDirection()
    {
      // 16 beats in section;
      // 2 tonic
      // 5 away
      // 5 home

      // 4 tonic
      int [] lBeatDirn = {0, 0, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 0, 0, 0, 0};

      assert(mBeatsLeftInSection <= 16);
      return lBeatDirn[16 - mBeatsLeftInSection];
    }

    /*
     * For a chord selected now, how far from the tonic are we allowed to be?
     * Zero means that the tonic chord is the only answer!
     */
    public int getMaxDist()
    {
      int [] lBeatDist = {0, 0, 5, 5, 5, 5, 5, 4, 3, 2, 1, 1, 0, 0, 0, 0};

      assert(mBeatsLeftInSection <= 16);
      return lBeatDist[16 - mBeatsLeftInSection];
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiTick) throws Exception
    {
      // Pass tick to base class.
      consumeMetronome(xiTick);
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

