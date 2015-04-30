package org.dogyjammers.jammypiece.components;

import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.Producer;

/**
 * Component to sink MIDI events in jammiepiece and output them to OS.
 */
public class MidiOut implements Consumer<MidiEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final Synthesizer mSynth;
  private final Receiver mReceiver;

  /**
   * Create a MidiOut device to play from the specified source.
   *
   * @param xiSource - the source.
   *
   * @throws MidiUnavailableException if the MIDI output device couldn't be opened.
   */
  public MidiOut(List<Producer<MidiEvent>> xiSources) throws MidiUnavailableException, InvalidMidiDataException
  {
    mSynth = MidiSystem.getSynthesizer();
    mSynth.open();
    LOGGER.info("Opened synth: " + mSynth.getDeviceInfo().getName() + " " + mSynth.getDeviceInfo().getDescription());

    // Check whether time-stamping is supported.
    if (mSynth.getMicrosecondPosition() == -1)
    {
      LOGGER.warn("Timestamping not supported by MidiOut");
    }

    mReceiver = mSynth.getReceiver();

    Instrument[] lLoadedInstruments = mSynth.getLoadedInstruments();
    for (int lii = 0; lii < lLoadedInstruments.length; lii++)
    {
      LOGGER.info("Instrument " + lii + ": " + lLoadedInstruments[lii].getName());
    }

    // Play a different instrument.
    // Instrument lInstrument = lLoadedInstruments[55];
    // mSynth.getChannels()[0].programChange(lInstrument.getPatch().getBank(), lInstrument.getPatch().getProgram());
    // mReceiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 109, 0), -1);

    // Register as a consumer of events from all sources.
    for (Producer<MidiEvent> lSource : xiSources)
    {
      lSource.registerConsumer(this);
    }
  }

  @Override
  public void consume(MidiEvent xiItem)
  {
    mReceiver.send(xiItem.getMessage(), xiItem.getTick());
  }
}
