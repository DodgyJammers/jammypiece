package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.ChordChangeEvent;
import org.dogyjammers.jammypiece.events.KeyChangeEvent;
import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class ChordSelector extends Distributor<ChordChangeEvent> implements Consumer<MidiEvent>
{
  private final KeyChangeListener mKeyChangeListener;
  private final MetronomeListener mMetronomeListener;

  public ChordSelector(Producer<MidiEvent> xiMelodySource,
                       Producer<KeyChangeEvent> xiKeySource,
                       Producer<TickEvent> xiMetronome)
  {
    mKeyChangeListener = new KeyChangeListener();
    mMetronomeListener = new MetronomeListener();

    xiMelodySource.registerConsumer(this);
    xiKeySource.registerConsumer(mKeyChangeListener);
    xiMetronome.registerConsumer(mMetronomeListener);
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    // Discard events.
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {
    @Override
    public void consume(TickEvent xiTick)
    {
      // Discard metronome events.
    }
  }

  private class KeyChangeListener implements Consumer<KeyChangeEvent>
  {
    @Override
    public void consume(KeyChangeEvent xiTick)
    {
      // Discard key change events.
    }
  }
}

