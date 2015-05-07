package org.dodgyjammers.jammypiece.components;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class Harmoniser extends Distributor<RichMidiEvent> implements Consumer<RichMidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;

  public Harmoniser(Producer<RichMidiEvent> xiMelodySource,
                    Producer<ChordChangeEvent> xiChordSource,
                    Producer<TickEvent> xiMetronome)
  {
    mChordListener = new ChordListener();
    mMetronomeListener = new MetronomeListener();

    xiMelodySource.registerConsumer(this);
    xiChordSource.registerConsumer(mChordListener);
    xiMetronome.registerConsumer(mMetronomeListener);
  }

  @Override
  public void consume(RichMidiEvent xiItem)
  {
    // Discard melody events.
  }

  private class ChordListener implements Consumer<ChordChangeEvent>
  {
    @Override
    public void consume(ChordChangeEvent xiItem)
    {
      // Discard chord events.
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {

    @Override
    public void consume(TickEvent xiItem)
    {
      // Discard metronome events.
    }
  }
}
