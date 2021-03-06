package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.vamp_plugins.*;

import java.util.*;

/**
 * Wrapper for Chordino Vamp plugin http://www.isophonics.net/nnls-chroma
 */

/*
 * Implements Chordino plugin using JVamp wrappers
 * http://www.isophonics.net/nnls-chroma
 * https://code.soundsoftware.ac.uk/projects/jvamp
 *
 * Chordino Plugin (Excerpts from http://www.isophonics.net)
 * 
 * - chord transcription, based on NNLS Chroma plugin
 * - chord profiles from the chord dictionary
 * - smoothing using:
 * -- chord change method
 * -- HMM/Viterbi approach
 *
 * parameters
 * - use NNLS chroma transcription: on or off (for linear spectral mapping)
 * -- preferred: on
 * * - spectral roll-on: 0.0-5.0% removing the low-frequency noise, useful for quiet recordings
 * -- preferred: 1.0%
 * - tuning mode: global or local, how to find out the tuning - locally or globally
 * -- preferred: global
 * - spectral whitening: 0.0-1.0 defines how much is the log-frequency spectrum whitened
 * -- preferred: 1.0
 * - spectral shape: 0.5-0.9 - shape of a note (amplitude has a decreasing pattern)
 * -- preferred: 0.7
 * - chroma normalisation: none/max/L1/L2
 * -- determines the type of chroma normalisation
 * - boost likelihood of the N label: Higher values lead to non-harmonic parts being recognized as 'no chord'
 * -- preferred: 0.1
 * - use Harte syntax: as described on ISMIR 2005
 * -- preferred: off
 *
 * outputNumber
 * 0: simplechord - Chord estimate
 * 1: chordnotes - Note representation of a chord estimate in MIDI
 * 2: harmonicchange - Indication of the likelihood of harmonic change
 * 3: loglikelihood - Log likelihood of harmonic change
 */

public class ChordinoLabelsPlugin extends SegmentationVampPlugin {
	public ChordinoLabelsPlugin() throws PluginLoader.LoadFailedException {
		key = "nnls-chroma:chordino";
		name = "Chordino";
		description = "Chordino VAMP Plugin - outputting chord labels";
		outputNumber = 0;
		outputType = OutputType.VALUE_AND_LABEL;
		blockSize = 16384;

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add(""); // Plugin handles raw WAV files
		inputFileExtension = ".wav";

		outputFileSuffix = "-chordino-labels";
		outputFileExtension = ".txt";

		parameters = new HashMap<>();
		parameters.put("useNNLS", (float) 1.0);
		parameters.put("rollon", (float) 1.0);
		parameters.put("tuningmode", (float) 0.0);
		parameters.put("whitening", (float) 1.0);
		parameters.put("s", (float) 0.7);
		parameters.put("boostn", (float) 0.1);
		parameters.put("usehartesyntax", (float) 0.0);

		p = loader.loadPlugin(key, defaultRate, adapterFlag);
		setParameters();
	}
}
