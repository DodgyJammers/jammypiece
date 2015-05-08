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
import org.dodgyjammers.jammypiece.components.MonkeyCage;
import org.dodgyjammers.jammypiece.components.TempoDetector;
import org.dodgyjammers.jammypiece.components.TimeSignatureDetector;
import org.dodgyjammers.jammypiece.events.ChordChangeEvent;
import org.dodgyjammers.jammypiece.events.KeyChangeEvent;
import org.dodgyjammers.jammypiece.events.RichMidiEvent;
import org.dodgyjammers.jammypiece.events.TempoChangeEvent;
import org.dodgyjammers.jammypiece.events.TimeSignatureChangeEvent;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;
import org.dodgyjammers.jammypiece.infra.Merger;
import org.dodgyjammers.jammypiece.infra.Producer;
import org.dodgyjammers.jammypiece.infra.WsLogServer;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static volatile Thread sMainThread;

  public static void main(String[] args) throws Exception
  {
    sMainThread = Thread.currentThread();
    MonkeyCage lRecorder = null;
    try
    {
      // Initialise log server.
      WsLogServer.init();

      // Create all the components and join them up.

      Producer<RichMidiEvent> lSource;
      String lSourceName = MachineSpecificConfiguration.getCfgVal(CfgItem.MIDI_IN_DEVICE, null);
      List<Producer<RichMidiEvent>> lSources;
      if ("dummy".equals(lSourceName))
      {
        lSources = Collections.singletonList((Producer<RichMidiEvent>)new DummyMidiSource());
      }
      else if ("null".equals(lSourceName))
      {
        lSources = Collections.emptyList(); 
      }
      else
      {
        MidiIn lMidiIn = new MidiIn();
        lSources = Collections.singletonList((Producer<RichMidiEvent>)lMidiIn);
        lMidiIn.start();
      }
      Producer<RichMidiEvent> lInputSelector = new InputSelector(lSources);
      Producer<RichMidiEvent> lJunkFilter = new JunkFilter(lInputSelector);
      new MidiEventDumper(lJunkFilter, MidiIn.class.getName());
      Producer<KeyChangeEvent> lKeyDetector = new KeyDetector(lJunkFilter);
      TempoDetector lTempoDetector = new TempoDetector(lJunkFilter);
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

      Merger<RichMidiEvent> lOutputs = new Merger<>();
      lOutputs.add(lAdjuster);
      lOutputs.add(lHarmoniser);
      lOutputs.add(lClicker);
      lOutputs.add(lCommandHandler);
      MidiOut lMidiOut = new MidiOut(Collections.singletonList((Producer<RichMidiEvent>)lOutputs));
      
      lRecorder = new MonkeyCage(lTempoDetector.current());  // TODO: adapt to tempo change
      lOutputs.registerConsumer(lRecorder);
      
      lMetronome.setClockSource(lMidiOut);


      // Start all the components that need to be started.
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
    if (lRecorder != null) {
      lRecorder.write();
    }
    System.exit(0);
  }
  
  public static void quit()
  {
    sMainThread.interrupt();
  }
}
