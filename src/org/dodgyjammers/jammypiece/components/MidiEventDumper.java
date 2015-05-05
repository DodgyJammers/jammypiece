// MidiEventDumper.java
// (C) COPYRIGHT METASWITCH NETWORKS 2015
package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Receiver;

import org.dodgyjammers.jammypiece.infra.Consumer;
import org.jsresources.midi.DumpReceiver;

public class MidiEventDumper implements Consumer<MidiEvent> {
  private Receiver mDumpReceiver;
  
  public MidiEventDumper() {
    mDumpReceiver = new DumpReceiver(System.out);
  }

  @Override
  public void consume(MidiEvent xiItem) {
    mDumpReceiver.send(xiItem.getMessage(), xiItem.getTick());
  }
}
