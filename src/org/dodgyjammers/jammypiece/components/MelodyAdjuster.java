package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.TickEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.Producer;

public class MelodyAdjuster extends Distributor<MidiEvent> implements Consumer<MidiEvent>
{
  private final ChordListener mChordListener;
  private final MetronomeListener mMetronomeListener;

  public MelodyAdjuster(Producer<MidiEvent> xiMelodySource,
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
  public void consume(MidiEvent xiItem) throws Exception
  {
    // Pass through melody events.
    // Be really annoying and swap C4 and C#4.
    if (xiItem.getMessage().getStatus() == 0x90 && xiItem.getMessage().getMessage()[1] == 0x3C) {
      xiItem = new MidiEvent(new ShortMessage(0x90, 0x3D, xiItem.getMessage().getMessage()[2]), xiItem.getTick());
    }
    distribute(xiItem);
  }

  private class ChordListener implements Consumer<ChordChangeEvent>
  {
    @Override
    public void consume(ChordChangeEvent xiItem) throws Exception
    {
      // Discard chord events.
    }
  }

  private class MetronomeListener implements Consumer<TickEvent>
  {

    @Override
    public void consume(TickEvent xiItem) throws Exception
    {
      // Discard metronome events.
    }
  }
}
