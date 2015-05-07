package org.dodgyjammers.jammypiece;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.components.ChordSelector;
import org.dodgyjammers.jammypiece.components.Clicker;
import org.dodgyjammers.jammypiece.components.DummyMidiSource;
import org.dodgyjammers.jammypiece.components.Harmoniser;
import org.dodgyjammers.jammypiece.components.InputSelector;
import org.dodgyjammers.jammypiece.components.JunkFilter;
import org.dodgyjammers.jammypiece.components.KeyDetector;
import org.dodgyjammers.jammypiece.components.MelodyAdjuster;
import org.dodgyjammers.jammypiece.components.Metronome;
import org.dodgyjammers.jammypiece.components.MidiEventDumper;
import org.dodgyjammers.jammypiece.components.MidiIn;
import org.dodgyjammers.jammypiece.components.MidiOut;
import org.dodgyjammers.jammypiece.components.TempoDetector;
import org.dodgyjammers.jammypiece.components.TimeSignatureDetector;
import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.WsLogServer;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();

  @SuppressWarnings("unused")
  public static void main(String[] args)
  {
    try
    {
      // Initialise log server.
      WsLogServer wsLogServer = WsLogServer.INSTANCE;
      Thread.sleep(5000);
      
      // Create all the components and join them up.

      Producer<RichMidiEvent> lSource;
      if ("dummy".equals(MachineSpecificConfiguration.getCfgVal(CfgItem.MIDI_IN_DEVICE, null)))
      {
        lSource = new DummyMidiSource();
      }
      else
      {
        lSource = new MidiIn();
      }
      List<Producer<RichMidiEvent>> lSources = Collections.singletonList(lSource);
      Producer<RichMidiEvent> lInputSelector = new InputSelector(lSources);
      Producer<RichMidiEvent> lJunkFilter = new JunkFilter(lInputSelector);
      new MidiEventDumper(lJunkFilter, MidiIn.class.getName());
      Producer<KeyChangeEvent> lKeyDetector = new KeyDetector(lJunkFilter);
      Producer<TempoChangeEvent> lTempoDetector = new TempoDetector(lJunkFilter);
      Producer<TimeSignatureChangeEvent> lTimeSigDetector = new TimeSignatureDetector(lJunkFilter, lTempoDetector);
      Metronome lMetronome = new Metronome(lTempoDetector, lTimeSigDetector);
      Producer<RichMidiEvent> lClicker = new Clicker(lMetronome);
      Producer<ChordChangeEvent> lChordSelector = new ChordSelector(lJunkFilter, lKeyDetector, lMetronome);
      Producer<RichMidiEvent> lAdjuster = new MelodyAdjuster(lJunkFilter, lChordSelector, lMetronome);
      Producer<RichMidiEvent> lHarmoniser = new Harmoniser(lAdjuster, lChordSelector, lMetronome);
      new MidiEventDumper(lAdjuster, MelodyAdjuster.class.getName());
      new MidiEventDumper(lHarmoniser, Harmoniser.class.getName());
      new MidiEventDumper(lClicker, Clicker.class.getName());
      List<Producer<RichMidiEvent>> lOutputs = new LinkedList<>();
      lOutputs.add(lAdjuster);
      lOutputs.add(lHarmoniser);
      lOutputs.add(lClicker);
      MidiOut lMidiOut = new MidiOut(lOutputs);
      lMetronome.setClockSource(lMidiOut);

      // Start all the components that need to be started.
      lSource.start();
      lMetronome.start();
      LOGGER.info("jammypiece started");

      // Spin until interrupted.
      while (true)
      {
        Thread.sleep(30000);
      }
    }
    catch (InterruptedException lEx)
    {
      LOGGER.info("Main thread interrupted - exiting");
    }
    catch (Exception lEx)
    {
      LOGGER.error("jammypiece failed to start", lEx);
    }
  }
}
