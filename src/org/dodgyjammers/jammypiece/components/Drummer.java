package org.dodgyjammers.jammypiece.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;

public class Drummer extends Distributor<RichMidiEvent> {
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static final boolean ENABLED = MachineSpecificConfiguration.getCfgVal(CfgItem.DRUMMER_ENABLED, false);
  private static final int CHANNEL = MachineSpecificConfiguration.getCfgVal(CfgItem.DRUMMER_CHANNEL, 9);

  private Pattern mPattern;
  
  private int mClicksLeft;
  private int mNumBeats;  // in section

  private int mPhase;  // kind of section: A(1), B(2)

  private int mSection;
  private int mNumSections;  // in song

  public Drummer(Producer<TickEvent> xiTicker, Producer<ChordChangeEvent> xiChorder) {
    mPattern = Pattern.valueOf(MachineSpecificConfiguration.getCfgVal(CfgItem.DRUMMER_PATTERN, "BO_DIDDLEY"));
    
    if (ENABLED) {
      xiTicker.registerConsumer(new TickListener());
      xiChorder.registerConsumer(new ChordListener());
    }
  }
  
  private void thump(TickEvent xiTick)
  {
    int lBeat = ((mNumBeats * xiTick.mTicksPerBeat) - mClicksLeft) / xiTick.mTicksPerBeat;  // TODO: assumes evenly divisible
    int lClick = (xiTick.mTickInBeat * mPattern.mClicksPerBeat / xiTick.mTicksPerBeat) + (lBeat % 8) * mPattern.mClicksPerBeat;
    LOGGER.debug("Thump {} - beat {}/{}, phase {}, section {}/{}", xiTick, lBeat, mNumBeats, mPhase, mSection, mNumSections);
    for (Px lWhat : mPattern.mSchedule.get(lClick)) {
      distribute(RichMidiEvent.makeNoteOn(CHANNEL, lWhat.mNoteVal, lWhat.mVelocity));
      // TODO: note off
    }
    mClicksLeft--;
  }
  
  private class TickListener implements Consumer<TickEvent>
  {

    @Override
    public void consume(TickEvent xiItem) throws Exception {
      thump(xiItem);
    }
  }
  
  private class ChordListener implements Consumer<ChordChangeEvent>
  {
    @Override
    public void consume(ChordChangeEvent xiChord) throws Exception {
      LOGGER.debug("Chord change {}", xiChord);
      mClicksLeft = xiChord.mClicksTilSection;
      mNumBeats = xiChord.mSectionLength;
      mSection = xiChord.mSection;
      mPhase = xiChord.mStructure[mSection];
      mNumSections = xiChord.mStructure.length;
    }
  }
  
  /**
   * Percussion (GM1 http://www.midi.org/techspecs/gm1sound.php).
   */
  private static enum Px
  {
    BASS(36),
    SNARE(38, 20),
    SNARE_STRESSED(SNARE.mNoteVal, 40),
    OPENHAT(46),
    CLOSEDHAT(42),
    PEDALHAT(44);
    
    private int mNoteVal;
    private int mVelocity;
    
    private Px(int xiNoteVal) {
      this(xiNoteVal, 64);
    }
    
    private Px(int xiNoteVal, int xiVelocity) {
      mNoteVal = xiNoteVal;
      mVelocity = xiVelocity;
    }
  }  
  
  public static class Pattern
  {
    private static List<Pattern> sPatterns = new ArrayList<>();
    
    private String mName;
    private int mClicksPerBeat;
    private int mNumClicks;
    private ArrayList<List<Px>> mSchedule;
    
    // http://www.learndrumsnow.com/technique/6-simple-but-powerful-drum-beats-part-1
    // http://www.learndrumsnow.com/technique/6-simple-but-powerful-drum-beats-part-2
    // http://www.learndrumsnow.com/playing-music/the-key-to-reading-drum-music
    
    static {
      new Pattern("EIGHTH_NOTE", 8, 2)
        .add(Px.BASS, 0, 2, 4, 6, 8, 10, 12, 14)
        .add(Px.SNARE, 2, 6, 10, 14)
        .addAll(Px.CLOSEDHAT);
   
      new Pattern("SUPER_SLOW", 8, 4)
        .add(Px.BASS, 0, 8, 16, 24)
        .add(Px.SNARE, 4, 12, 20, 28)
        .addAll(Px.CLOSEDHAT);
    
      new Pattern("SUPER_FAST", 8, 1)
        .add(Px.BASS, 0, 2, 4, 6)
        .add(Px.SNARE, 1, 3, 5, 7)
        .addAll(Px.CLOSEDHAT);
    
      new Pattern("SLOW_BLUES", 8, 3)
        .add(Px.BASS, 0, 5, 6, 12, 17, 18)
        .add(Px.SNARE, 3, 9, 15, 21)
        .addAll(Px.CLOSEDHAT);
    
      new Pattern("FAST_BLUES_SHUFFLE", 8, 3)  // or notate as swing
        .add(Px.BASS, 0, 3, 6, 9, 12, 15, 18, 21)
        .add(Px.SNARE, 3, 9, 15, 21)
        .add(Px.CLOSEDHAT, 0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21, 23);
    
      new Pattern("BO_DIDDLEY", 8, 4)
        .add(Px.BASS, 0, 4, 8, 12, 16, 20, 24, 28)
        .add(Px.PEDALHAT, 4, 12, 20, 28)
        .add(Px.SNARE_STRESSED, 0,       3,       6,          10,     12,             16,         19,         22,             26,     28)
        .add(Px.SNARE,             1, 2,    4, 5,    7, 8, 9,     11,     13, 14, 15,     17, 18,     20, 21,     23, 24, 25,     27,     29, 30, 31);
    }

    public Pattern(String xiName, int xiNumBeats, int xiClicksPerBeat) {
      mName = xiName;
      mClicksPerBeat = xiClicksPerBeat;
      mNumClicks = xiClicksPerBeat * xiNumBeats;
      mSchedule = new ArrayList<List<Px>>(mNumClicks);
      for (int i = 0; i < mNumClicks; i++) {
        mSchedule.add(new ArrayList<Px>());
      }
      sPatterns.add(this);
    }

    /**
     * When (0-origin clicks) to play what instrument.
     * @param xiWhat
     * @param xiWhen
     * @return
     */
    public Pattern add(Px xiWhat, int... xiWhen) {
      for (int lWhen: xiWhen) {
        mSchedule.get(lWhen).add(xiWhat);
      }
      return this;
    }
    
    public Pattern addAll(Px xiWhat) {
      for (int i = 0; i < mNumClicks; i++) {
        mSchedule.get(i).add(xiWhat);
      }
      return this;
    }
    
    public static Pattern valueOf(String xiName)
    {
      for (Pattern lPattern : sPatterns)
      {
        if (lPattern.mName.equals(xiName))
        {
          return lPattern;
        }
      }
      throw new IllegalArgumentException("Unknown pattern name " + xiName);
    }
    
    public static class Part {
      private Px mWhat;
      private int[] mWhen;
      public Part(Px xiWhat, int[] xiWhen) {
        mWhat = xiWhat;
        mWhen = xiWhen;
      }
    }
    
    
//    public 
  }
  
}
