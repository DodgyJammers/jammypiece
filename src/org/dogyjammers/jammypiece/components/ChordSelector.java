package org.dogyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;

import org.dogyjammers.jammypiece.events.ChordChange;
import org.dogyjammers.jammypiece.events.KeyChange;
import org.dogyjammers.jammypiece.events.Tick;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Distributor;
import org.dogyjammers.jammypiece.infra.Producer;

public class ChordSelector extends Distributor<ChordChange> implements Consumer<MidiEvent>
{
  public ChordSelector(Producer<MidiEvent> xiMelodySource,
                       Producer<KeyChange> xiKeySource,
                       Producer<Tick> xiMetronome)
  {
    // !! ARR Auto-generated constructor stub
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    // Discard events.
  }
}

