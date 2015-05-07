package org.dodgyjammers.jammypiece.components;

import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.infra.Consumer;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Component to sink MIDI events in jammiepiece and output them to OS.
 */
public class MidiOut implements Consumer<RichMidiEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final MidiDevice mDevice;
  private final Receiver mMidiOut;

  /**
   * Create a MidiOut device to play from the specified source.
   *
   * @param xiSources - the MIDI event sources.  These are mixed together to form the output.
   *
   * @throws MidiUnavailableException if the MIDI output device couldn't be opened.
   */
  public MidiOut(List<Producer<RichMidiEvent>> xiSources) throws MidiUnavailableException
  {
    // Get the configured MIDI device (or the default device if none is configured).
    String lConfiguredDeviceStr = MachineSpecificConfiguration.getCfgVal(CfgItem.MIDI_OUT_DEVICE, null);

    // Find the configured device.
    MidiDevice lDevice = null;
    Receiver lMidiOut = null;
    for (Info lDeviceInfo : MidiSystem.getMidiDeviceInfo())
    {
      String lDeviceStr = lDeviceInfo.getVendor() + " " + lDeviceInfo.getName() + " " + lDeviceInfo.getDescription();
      MidiDevice lTmpDevice = MidiSystem.getMidiDevice(lDeviceInfo);

      if (lTmpDevice.getMaxReceivers() == 0)
      {
        LOGGER.info("Skipping device: " + lDeviceStr);
      }
      else
      {
        LOGGER.info("Found rx device: " + lDeviceStr);

        if ((lConfiguredDeviceStr != null) && (lConfiguredDeviceStr.equals(lDeviceStr)))
        {
          lDevice = lTmpDevice;
          lMidiOut = lDevice.getReceiver();
        }
      }
    }

    if (lDevice == null)
    {
      LOGGER.warn("No configured MIDI output device - using default");
      lMidiOut = MidiSystem.getReceiver();
    }
    else
    {
      lDevice.open();
      LOGGER.info("Opened synth: " + lDevice.getDeviceInfo().getVendor() + " " +
                                     lDevice.getDeviceInfo().getName() + " " +
                                     lDevice.getDeviceInfo().getDescription());

      // Check whether time-stamping is supported.
      if (lDevice.getMicrosecondPosition() == -1)
      {
        LOGGER.warn("Timestamping not supported by MidiOut");
      }
    }

    mDevice = lDevice;
    mMidiOut = lMidiOut;

    // Dump details of the chosen device.
    dumpDeviceDetails();

    // Register as a consumer of events from all sources.
    for (Producer<RichMidiEvent> lSource : xiSources)
    {
      lSource.registerConsumer(this);
    }
  }

  @Override
  public void consume(RichMidiEvent xiItem) throws Exception
  {
    mMidiOut.send(xiItem.getMessage(), xiItem.getTick());
  }

  /**
   * @return the microsecond position of the output device.
   */
  public long getMicrosecondPosition()
  {
    return mDevice.getMicrosecondPosition();
  }

  private void dumpDeviceDetails()
  {
    try
    {
      Instrument[] lInstruments = MidiSystem.getSynthesizer().getAvailableInstruments();
      for (Instrument lInstrument : lInstruments)
      {
        Patch lPatch = lInstrument.getPatch();
        LOGGER.debug("Instrument " + lPatch.getBank() + "/" + lPatch.getProgram() + ": " + lInstrument.getName());
      }
    }
    catch (MidiUnavailableException lEx)
    {
      LOGGER.warn("Couldn't enumerate instruments", lEx);
    }
  }
}
