package org.dodgyjammers.jammypiece.components;

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

  private int mClicksLeft;
  private int mNumBeats;  // in section

  private int mPhase;  // kind of section: A(1), B(2)

  private int mSection;
  private int mNumSections;  // in song

  public Drummer(Producer<TickEvent> xiTicker, Producer<ChordChangeEvent> xiChorder) {
    if (ENABLED) {
      xiTicker.registerConsumer(new TickListener());
      xiChorder.registerConsumer(new ChordListener());
    }
  }
  
  private void thump(TickEvent xiTick)
  {
    int lBeat = ((mNumBeats * xiTick.mTicksPerBeat) - mClicksLeft) / xiTick.mTicksPerBeat;
    LOGGER.debug("Thump {} - beat {}/{}, phase {}, section {}/{}", xiTick, lBeat, mNumBeats, mPhase, mSection, mNumSections);
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
      mSection = 0; // TODO xiChord.mSection;
      mPhase = xiChord.mStructure[mSection];
      mNumSections = xiChord.mStructure.length;
    }
  }  
}
