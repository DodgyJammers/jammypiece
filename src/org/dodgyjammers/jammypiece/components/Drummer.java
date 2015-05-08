package org.dodgyjammers.jammypiece.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class Drummer extends Distributor<RichMidiEvent> {
  private static final Logger LOGGER = LogManager.getLogger();
  
  private ChordChangeEvent mChord;

  public Drummer(Producer<TickEvent> xiTicker, Producer<ChordChangeEvent> xiChorder) {
    // TODO Auto-generated constructor stub
  }
  
  private void thump(TickEvent xiTick)
  {
    LOGGER.debug("Thump {} on {}", xiTick, mChord);
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
    public void consume(ChordChangeEvent xiItem) throws Exception {
      mChord = xiItem;
    }
  }  
}
