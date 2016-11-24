package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.application.visualizations.DrawPanelFactory;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.*;

import static org.mockito.Mockito.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for AudioAnalyser class
 */

@SuppressWarnings("ConstantConditions")

public class AudioAnalyserTest {
	private AudioAnalyser audioAnalyser;
	private DrawPanelFactory drawPanelFactory;
	private String wrongInputFile;
	private File testWavFile, testReportFixture;
	private String resultFile;

	@Before
	public void setUp() {
		drawPanelFactory = new DrawPanelFactory();
		wrongInputFile = "wrongfile";
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath());
		testReportFixture = new File(classLoader.getResource("test-printPluginsFixture.txt").getFile());
	}

	@Test(expected = AudioAnalyser.IncorrectInputException.class)
	public void shouldThrowExceptionOnWrongFile() throws IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		AnalysisPluginFactory analysisPluginFactory = new AnalysisPluginFactory();
		audioAnalyser = new AudioAnalyser(analysisPluginFactory, drawPanelFactory);
		audioAnalyser.runAnalysis(wrongInputFile, "chord_analyser:average_chord_complexity_distance", true, false);
	}

	@Test
	public void shouldPrintPlugins() throws IOException {
		String[] availablePlugins = { "test_plugin" };
		String[] visualPlugins = { "visual_plugin" };
		AnalysisPluginFactory analysisPluginFactory = mock(AnalysisPluginFactory.class);
		when(analysisPluginFactory.getAvailablePlugins()).thenReturn(availablePlugins);
		DrawPanelFactory drawPanelFactory = mock(DrawPanelFactory.class);
		when(drawPanelFactory.getAllVisualizations()).thenReturn(visualPlugins);

		audioAnalyser = new AudioAnalyser(analysisPluginFactory, drawPanelFactory) {
			public String printInstalledVampPlugins() {
				return "\nINSTALLED_VAMP_PLUGINS_FOLLOW\n";
			}
		};

		BufferedReader readerFixture = new BufferedReader(new FileReader(testReportFixture));
		StringBuilder fixtureString = new StringBuilder();
		String line;
		while ((line = readerFixture.readLine()) != null) { // Check for null is valid
			fixtureString.append(line).append("\n");
		}

		assertEquals(fixtureString.toString(), audioAnalyser.printPlugins());
	}

	@Test
	public void shouldCallPluginAnalyse() throws IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		AnalysisPlugin analysisPlugin = mock(AnalysisPlugin.class);
		when(analysisPlugin.analyse(testWavFile.toString(), true, false)).thenReturn("Done!");

		AnalysisPluginFactory analysisPluginFactory = new AnalysisPluginFactory() {
			public AnalysisPlugin createPlugin(String pluginKey) {
				return analysisPlugin;
			}
		};
		audioAnalyser = new AudioAnalyser(analysisPluginFactory, drawPanelFactory);

		assertEquals("Done!", audioAnalyser.runAnalysis(testWavFile.toString(), "chord_analyser:average_chord_complexity_distance", true, false));
	}
}