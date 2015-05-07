package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.Receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.jsresources.midi.DumpReceiver;

public class MidiEventDumper implements Consumer<RichMidiEvent>
{
  private final Receiver mDumpReceiver;

  public MidiEventDumper(Producer<RichMidiEvent> xiSource, String xiLoggerName)
  {
    final Logger LOGGER = LogManager.getLogger(xiLoggerName);
    mDumpReceiver = new DumpReceiver(new DumpReceiver.Sink() {
      @Override
      public void apply(String xiMsg) {
        LOGGER.info(xiMsg);
      }
    });
    xiSource.registerConsumer(this);
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    mDumpReceiver.send(xiItem.getMessage(), xiItem.getTick());
  }
}
