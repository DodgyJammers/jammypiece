package org.dodgyjammers.jammypiece;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dodgyjammers.jammypiece.components.ChordSelector;
import org.dodgyjammers.jammypiece.components.Clicker;
import org.dodgyjammers.jammypiece.components.CommandHandler;
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
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.infra.WsLogServer;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static volatile Thread sMainThread;

  public static void main(String[] args)
  {
    sMainThread = Thread.currentThread();
    try
    {
      // Initialise log server.
      WsLogServer.init();

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
      Producer<RichMidiEvent> lHarmoniser = new Harmoniser(lAdjuster, lChordSelector, lKeyDetector, lMetronome, lTimeSigDetector);
      new MidiEventDumper(lAdjuster, MelodyAdjuster.class.getName());
      new MidiEventDumper(lHarmoniser, Harmoniser.class.getName());
      new MidiEventDumper(lClicker, Clicker.class.getName());

      CommandHandler lCommandHandler = new CommandHandler();
      WsLogServer.INSTANCE.registerConsumer(lCommandHandler);

      List<Producer<RichMidiEvent>> lOutputs = new LinkedList<>();
      lOutputs.add(lAdjuster);
      lOutputs.add(lHarmoniser);
      lOutputs.add(lClicker);
      lOutputs.add(lCommandHandler);
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

    // Force quit because we have non-daemon threads, sigh.
    System.exit(0);
  }
  
  public static void quit()
  {
    sMainThread.interrupt();
  }
}
