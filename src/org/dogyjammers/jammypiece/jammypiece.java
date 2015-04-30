package org.dogyjammers.jammypiece;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.components.ChordSelector;
import org.dogyjammers.jammypiece.components.Clicker;
import org.dogyjammers.jammypiece.components.DummyMidiSource;
import org.dogyjammers.jammypiece.components.Harmoniser;
import org.dogyjammers.jammypiece.components.InputSelector;
import org.dogyjammers.jammypiece.components.KeyDetector;
import org.dogyjammers.jammypiece.components.MelodyAdjuster;
import org.dogyjammers.jammypiece.components.Metronome;
import org.dogyjammers.jammypiece.components.MidiOut;
import org.dogyjammers.jammypiece.components.TempoDetector;
import org.dogyjammers.jammypiece.events.ChordChangeEvent;
import org.dogyjammers.jammypiece.events.KeyChangeEvent;
import org.dogyjammers.jammypiece.events.TempoChangeEvent;
import org.dogyjammers.jammypiece.events.TickEvent;
import org.dogyjammers.jammypiece.infra.Producer;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args)
  {
    try
    {
      // Create all the components and join them up.
      DummyMidiSource lSource = new DummyMidiSource();
      List<Producer<MidiEvent>> lSources = Collections.singletonList((Producer<MidiEvent>)lSource);
      Producer<MidiEvent> lInputSelector = new InputSelector(lSources);
      Producer<KeyChangeEvent> lKeyDetector = new KeyDetector(lInputSelector);
      Producer<TempoChangeEvent> lTempoDetector = new TempoDetector(lInputSelector);
      Producer<TickEvent> lMetronome = new Metronome(lTempoDetector);
      Producer<MidiEvent> lClicker = new Clicker(lMetronome);
      Producer<ChordChangeEvent> lChordSelector = new ChordSelector(lInputSelector, lKeyDetector, lMetronome);
      Producer<MidiEvent> lAdjuster = new MelodyAdjuster(lInputSelector, lChordSelector, lMetronome);
      Producer<MidiEvent> lHarmoniser = new Harmoniser(lAdjuster, lChordSelector, lMetronome);
      List<Producer<MidiEvent>> lOutputs = new LinkedList<>();
      lOutputs.add(lAdjuster);
      lOutputs.add(lHarmoniser);
      lOutputs.add(lClicker);
      new MidiOut(lOutputs);

      // Start the source.
      lSource.start();
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
