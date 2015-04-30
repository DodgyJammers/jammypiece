package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.ChordChangeEvent;
import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class Harmoniser extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;

  public Harmoniser(Producer<MidiEvent> xiMelodySource,
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
  public void consume(MidiEvent xiItem)
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
