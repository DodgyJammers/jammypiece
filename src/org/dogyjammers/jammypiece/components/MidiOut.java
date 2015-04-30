package org.dogyjammers.jammypiece.components;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.infra.Consumer;
import org.dogyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dogyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dogyjammers.jammypiece.infra.Producer;

/**
 * Component to sink MIDI events in jammiepiece and output them to OS.
 */
public class MidiOut implements Consumer<MidiEvent>
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final Receiver mReceiver;

  /**
   * Create a MidiOut device to play from the specified source.
   *
   * @param xiSource - the source.
   *
   * @throws MidiUnavailableException if the MIDI output device couldn't be opened.
   */
  public MidiOut(List<Producer<MidiEvent>> xiSources) throws MidiUnavailableException
  {
    // Get the configured MIDI device (or the default device if none is configured).
    String lConfiguredDeviceStr = MachineSpecificConfiguration.getCfgVal(CfgItem.MIDI_OUT_DEVICE, null);

    // Find the configured device.
    MidiDevice lDevice = null;
    Receiver lReceiver = null;
    for (Info lDeviceInfo : MidiSystem.getMidiDeviceInfo())
    {
      String lDeviceStr = lDeviceInfo.getVendor() + " " + lDeviceInfo.getName() + " " + lDeviceInfo.getDescription();
      LOGGER.info("Found device: " + lDeviceStr);

      if ((lConfiguredDeviceStr != null) && (lConfiguredDeviceStr.equals(lDeviceStr)))
      {
        lDevice = MidiSystem.getMidiDevice(lDeviceInfo);
        lReceiver = lDevice.getReceiver();
      }
    }

    if (lDevice == null)
    {
      LOGGER.warn("No configured MIDI output device - using default");
      lReceiver = MidiSystem.getReceiver();
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

    mReceiver = lReceiver;

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
