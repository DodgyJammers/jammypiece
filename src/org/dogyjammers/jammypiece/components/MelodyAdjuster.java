package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.ChordChange;
import org.dogyjammers.jammypiece.events.Tick;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class MelodyAdjuster extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;

  public MelodyAdjuster(Producer<MidiEvent> xiMelodySource,
                        Producer<ChordChange> xiChordSource,
                        Producer<Tick> xiMetronome)
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
    // Pass through melody events.
    distribute(xiItem);
  }

  private class ChordListener implements Consumer<ChordChange>
  {
    @Override
    public void consume(ChordChange xiItem)
    {
      // Discard chord events.
    }
  }

  private class MetronomeListener implements Consumer<Tick>
  {

    @Override
    public void consume(Tick xiItem)
    {
      // Discard metronome events.
    }
  }
}
