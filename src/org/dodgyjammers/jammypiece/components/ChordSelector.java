package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.musickb.ChordMap;

public class ChordSelector extends Distributor<ChordChangeEvent> implements Consumer<RichMidiEvent>
{
  private final KeyChangeListener mKeyChangeListener;
  private final MetronomeListener mMetronomeListener;
  private final ChordMap mChordMap;

  public ChordSelector(Producer<RichMidiEvent> xiMelodySource,
                       Producer<KeyChangeEvent> xiKeySource,
                       Producer<TickEvent> xiMetronome)
  {
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();
    mChordMap = new ChordMap();

    xiMelodySource.registerConsumer(this);
    xiKeySource.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);

    //distribute(new ChordChangeEvent())
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    // Receive and discard melody events.
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiTick) throws Exception
    {
      // Discard metronome events.
    }
  }

  private class KeyChangeListener implements Consumer<KeyChangeEvent>
  {
    @Override
    public void consume(KeyChangeEvent xiTick) throws Exception
    {
      // Discard key change events.
    }
  }
}

