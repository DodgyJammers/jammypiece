package org.dodgyjammers.jammypiece.components;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.infra.Distributor;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;

/**
 * Component to sink MIDI events in jammiepiece and output them to OS.
 */
public class MidiIn extends Distributor<MidiEvent> implements Receiver
{
  private static final Logger LOGGER = LogManager.getLogger();

  private final Transmitter mMidiIn;

  /**
   * Create a MidiOut device to play from the specified source.
   *
   * @throws MidiUnavailableException if the configured MIDI input device couldn't be opened.
   */
  public MidiIn() throws MidiUnavailableException
  {
    // Get the configured MIDI device (or the default device if none is configured).
    String lConfiguredDeviceStr = MachineSpecificConfiguration.getCfgVal(CfgItem.MIDI_IN_DEVICE, null);

    // Find the configured device.
    MidiDevice lDevice = null;
    Transmitter lMidiIn = null;
    for (Info lDeviceInfo : MidiSystem.getMidiDeviceInfo())
    {
      String lDeviceStr = lDeviceInfo.getVendor() + " " + lDeviceInfo.getName() + " " + lDeviceInfo.getDescription();
      LOGGER.info("Found device: " + lDeviceStr);

      if ((lConfiguredDeviceStr != null) && (lConfiguredDeviceStr.equals(lDeviceStr)))
      {
        lDevice = MidiSystem.getMidiDevice(lDeviceInfo);
        lMidiIn = lDevice.getTransmitter();
      }
    }

    if (lDevice == null)
    {
      if (lConfiguredDeviceStr != null)
      {
        LOGGER.warn("Failed to find configured MIDI input device: " + lConfiguredDeviceStr);
      }
      else
      {
        LOGGER.warn("No configured MIDI input device - using default");
      }
      lMidiIn = MidiSystem.getTransmitter();
    }
    else
    {
      lDevice.open();
      LOGGER.info("Opened MIDI in: " + lDevice.getDeviceInfo().getVendor() + " " +
                                       lDevice.getDeviceInfo().getName() + " " +
                                       lDevice.getDeviceInfo().getDescription());

      // Check whether time-stamping is supported.
      if (lDevice.getMicrosecondPosition() == -1)
      {
        LOGGER.warn("Timestamping not supported by MidiIn");
      }
    }

    // Set ourselves as the receiver of MIDI events.
    mMidiIn = lMidiIn;
  }

  public void start()
  {
    mMidiIn.setReceiver(this);
  }

  @Override
  public void close()
  {
    mMidiIn.close();
  }

  @Override
  public void send(MidiMessage xiMessage, long xiTimestamp)
  {
    LOGGER.debug("Event received");
    distribute(new MidiEvent(xiMessage, -1));
  }
}
