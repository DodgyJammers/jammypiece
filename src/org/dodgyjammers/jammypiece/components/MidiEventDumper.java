package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Receiver;

import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.jsresources.midi.DumpReceiver;

public class MidiEventDumper implements Consumer<MidiEvent>
{
  private final Receiver mDumpReceiver;

  public MidiEventDumper(Producer<MidiEvent> xiSource)
  {
    mDumpReceiver = new DumpReceiver(new DumpReceiver.Sink() {
      @Override
      public void apply(String xiMsg) {
        System.out.println(xiMsg);
      }
    });
    xiSource.registerConsumer(this);
  }

  @Override
  public void consume(MidiEvent xiItem) throws Exception
  {
    mDumpReceiver.send(xiItem.getMessage(), xiItem.getTick());
  }
}
