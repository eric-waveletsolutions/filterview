package com.waveletsolutions.filtering;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * The {@code App} class is a JavaFX application that demonstrates signal processing concepts 
 * such as filtering and Fast Fourier Transform (FFT) analysis. 
 * 
 * This program allows users to control the speed of a motor (simulated via a slider), 
 * apply different types of filters (raised cosine and low-pass), and visualize the time-domain 
 * signal and its frequency-domain representation (FFT).
 * 
 * Key features include:
 * <ul>
 *   <li>A vertical slider to adjust the speed of the signal in real-time.</li>
 *   <li>A horizontal slider to control the beta value (roll-off factor) of the raised cosine filter.</li>
 *   <li>Radio buttons to switch between a raised cosine filter and a low-pass filter.</li>
 *   <li>An oscillation feature to automate speed changes in a square wave pattern.</li>
 *   <li>Real-time plots of both time-domain and frequency-domain (FFT) data.</li>
 * </ul>
 * 
 * This class is intended as an educational tool for students and professional engineers 
 * learning about signal processing techniques, such as filtering, in the context of real-time 
 * signal control and visualization.
 * It demonstrates the application of raised cosine and low-pass filters to signal data, 
 * as well as the spectral analysis of filtered and non-filtered signals using FFT.
 * 
 * <h2>Usage:</h2>
 * <ol>
 *   <li>Run the application and adjust the speed using the vertical slider.</li>
 *   <li>Switch between different filters using the radio buttons.</li>
 *   <li>Observe the changes in the time-domain plot and the FFT (frequency-domain) plot.</li>
 *   <li>Press the "Start Oscillation" button to automatically oscillate the speed between 0 and 100.</li>
 * </ol>
 * 
 * <h2>Technical Concepts Covered:</h2>
 * <ul>
 *   <li>Sampling rate: How often the signal is updated.</li>
 *   <li>Filter length: The number of recent samples used for filtering.</li>
 *   <li>Fast Fourier Transform (FFT): Converts a time-domain signal to the frequency domain.</li>
 *   <li>Raised cosine and low-pass filters: Techniques to shape and smooth the signal.</li>
 * </ul>
 * 
 * 
 * This application is designed to be simple and interactive, making it a useful tool 
 * for beginners in both programming and signal processing.
 * 
 * @author Eric Ratliff
 * @version 1.0.0
 * @since 2024-09-09
 */
public class App extends Application {

    // Series for plotting raw (unfiltered) and filtered data in the time domain
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> filteredSeries = new XYChart.Series<>();

    // Series for plotting the FFT (frequency domain) of unfiltered and filtered signals
    private XYChart.Series<Number, Number> fftSeriesUnfiltered = new XYChart.Series<>();
    private XYChart.Series<Number, Number> fftSeriesFiltered = new XYChart.Series<>();

    // Counter for tracking time (used for x-axis in time-domain plot)
    private int time = 0;

    // The sampling rate defines how many samples are collected per second (in Hz)
    private static final double SAMPLING_RATE = 500.0;  // 500 samples per second

    // Window size is how many samples we display at once in the time-domain plot
    private final int WINDOW_SIZE = 200;  // Shows the last 200 samples

    // Interval between each x-axis point in the time-domain plot
    private final double X_INTERVAL = 0.05;  // Spacing between time points (0.05 seconds)

    // Filter length refers to how many recent samples are used to smooth or filter the signal
    private final int FILTER_LENGTH = 8;  // Smaller filter length = faster transitions, less smoothing

    // FFT size refers to how many samples are used for frequency analysis (FFT)
    private final int FFT_SIZE = 512;  // Larger FFT size = better frequency resolution but more computation

    // Beta controls the sharpness of the raised cosine filter (only used if the raised cosine filter is selected)
    private double beta = 0.5;  // Lower beta = sharper transition for the raised cosine filter

    // Cutoff frequency for the low-pass filter (removes frequencies above this value)
    private double cutoffFrequency = 32.0;  // 32 Hz cutoff frequency for the low-pass filter

    // A flag to determine which filter to use (true for raised cosine, false for low-pass)
    private boolean useRaisedCosine = true;

    // Store recent raw samples for time-domain plotting and FFT calculation
    private List<Double> recentSamples = new ArrayList<>();

    // Store recent filtered samples for FFT calculation
    private List<Double> recentFilteredSamples = new ArrayList<>();

    // Counter to control how often the graph updates (to avoid too many updates)
    private int updateCount = 0;

    // Threshold to limit the frequency of graph updates (update graph every 4 slider changes)
    private final int UPDATE_THRESHOLD = 4;  // Reduces lag by updating graph less frequently

    /**
     * Resets the FFT (Fast Fourier Transform) series for both the unfiltered and filtered data.
     * <p>
     * This method clears the data in the FFT graphs and sets the values back to zero.
     * It is useful when the user stops interacting with the slider or when a filter change occurs.
     * The FFT series for both the raw (unfiltered) and filtered signals are updated here.
     */
    private void resetFFT() {
        // Clear the existing FFT data for both unfiltered and filtered series
        fftSeriesUnfiltered.getData().clear();
        fftSeriesFiltered.getData().clear();
        
        // Populate the FFT series with zeroes to reset the graph visually
        for (int i = -FFT_SIZE / 2; i < FFT_SIZE / 2; i++) {
            fftSeriesUnfiltered.getData().add(new XYChart.Data<>(i, 0));  // Reset to zero for unfiltered series
            fftSeriesFiltered.getData().add(new XYChart.Data<>(i, 0));    // Reset to zero for filtered series
        }
    }

    /**
     * The entry point for the JavaFX application.
     * <p>
     * This method sets up the user interface for controlling motor speed and viewing
     * the time-domain and frequency-domain (FFT) graphs. It includes sliders, filter options, 
     * and a button to oscillate the motor speed automatically.
     *
     * @param primaryStage The primary stage for this JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Slider for controlling motor speed
        Slider speedSlider = new Slider(-100, 100, 0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(50);
        speedSlider.setMinorTickCount(5);
        speedSlider.setOrientation(Orientation.VERTICAL);
        Label speedLabel = new Label("Speed");

        // Slider for adjusting beta (roll-off factor) for the raised cosine filter
        Slider betaSlider = new Slider(0.0, 1.0, beta);
        betaSlider.setShowTickLabels(true);
        betaSlider.setShowTickMarks(true);
        betaSlider.setMajorTickUnit(0.1);
        betaSlider.setMinorTickCount(10);
        Label betaLabel = new Label("Beta (Roll-off Factor)");
        Label betaValueLabel = new Label("Beta Value: " + beta);

        // Radio buttons for selecting between raised cosine and low-pass filters
        RadioButton raisedCosineRadio = new RadioButton("Raised Cosine");
        RadioButton lowPassRadio = new RadioButton("Low-Pass");
        ToggleGroup filterGroup = new ToggleGroup();
        raisedCosineRadio.setToggleGroup(filterGroup);
        lowPassRadio.setToggleGroup(filterGroup);
        raisedCosineRadio.setSelected(true);

        // Update graphs and FFT when filter selection changes
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == raisedCosineRadio) {
                useRaisedCosine = true;
            } else {
                useRaisedCosine = false;
            }
            updateGraph(speedSlider.getValue());
            updateFFT();
        });

        // Pause transition to reset the FFT after slider movement stops
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

        // Trigger FFT reset when slider stops moving
        pause.setOnFinished(event -> resetFFT());

        // Add listener to update the FFT when the slider changes and reset pause timer
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateCount++;
            if (updateCount % UPDATE_THRESHOLD == 0) {
                updateGraph(newValue.doubleValue());
                updateFFT();
                updateCount = 0;  // Reset update counter
            }
        });

        // Setup for time-domain graph (displays speed over time)
        NumberAxis xAxis = new NumberAxis(0, WINDOW_SIZE * X_INTERVAL, 5);
        NumberAxis yAxis = new NumberAxis(-100, 100, 10);
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        series.setName("Original Speed");
        filteredSeries.setName("Filtered Speed");
        lineChart.getData().add(series);
        lineChart.getData().add(filteredSeries);

        // Setup for frequency-domain (FFT) graph
        NumberAxis freqAxis = new NumberAxis(-FFT_SIZE / 2, FFT_SIZE / 2 - 1, FFT_SIZE / 16);
        NumberAxis magAxis = new NumberAxis(0, 5000, 1000);  // Constant y-axis for FFT
        LineChart<Number, Number> fftChart = new LineChart<>(freqAxis, magAxis);
        fftChart.setTitle("Frequency Domain");
        fftSeriesUnfiltered.setName("FFT (Unfiltered)");
        fftSeriesFiltered.setName("FFT (Filtered)");
        fftChart.getData().add(fftSeriesUnfiltered);
        fftChart.getData().add(fftSeriesFiltered);
        fftChart.setCreateSymbols(false);

        // Pre-fill the time-domain chart with zeros
        for (int i = 0; i < WINDOW_SIZE; i++) {
            series.getData().add(new XYChart.Data<>(i * X_INTERVAL, 0));
            filteredSeries.getData().add(new XYChart.Data<>(i * X_INTERVAL, 0));
            recentSamples.add(0.0);
            recentFilteredSamples.add(0.0);  // Initialize filtered samples with zeros
        }
        time = WINDOW_SIZE;

        // Timeline for continuously updating the time-domain graph
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            double speed = speedSlider.getValue();
            beta = betaSlider.getValue();
            betaValueLabel.setText("Beta Value: " + String.format("%.2f", beta));
            updateGraph(speed);  // Update time-domain graph only
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Button to toggle between manual control and automatic oscillation
        Button oscillateButton = new Button("Start Oscillation");

        // Timeline for oscillating motor speed (square wave)
        Timeline oscillationTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            double frequency = 0.5;  // 0.5 Hz = 2 seconds per cycle
            double timeInMillis = System.currentTimeMillis();
            double period = 1000 / frequency;  // Period of the square wave in milliseconds

            // Alternate between 0 and 100 (square wave logic)
            double oscillatedSpeed = (timeInMillis % period) < (period / 2) ? 0 : 100;
            speedSlider.setValue(oscillatedSpeed);  // Update slider automatically
            updateFFT();  // Update FFT based on oscillating speed
        }));
        oscillationTimeline.setCycleCount(Timeline.INDEFINITE);

        // Toggle between oscillation and manual control when button is pressed
        oscillateButton.setOnAction(event -> {
            if (oscillationTimeline.getStatus() == Animation.Status.RUNNING) {
                oscillationTimeline.stop();  // Stop oscillation
                oscillateButton.setText("Start Oscillation");
                speedSlider.setDisable(false);  // Re-enable manual control
            } else {
                oscillationTimeline.play();  // Start oscillation
                oscillateButton.setText("Stop Oscillation");
                speedSlider.setDisable(true);  // Disable manual control
            }
        });

        // Layout for controls and graphs
        VBox controlLayout = new VBox(10, raisedCosineRadio, lowPassRadio, betaLabel, betaSlider, betaValueLabel, oscillateButton);
        controlLayout.setAlignment(Pos.TOP_LEFT);

        VBox speedLayout = new VBox(10, speedLabel, speedSlider);
        speedLayout.setAlignment(Pos.CENTER);

        // Arrange the layout in the main window
        BorderPane layout = new BorderPane();
        layout.setLeft(speedLayout);
        layout.setRight(controlLayout);
        layout.setCenter(lineChart);
        layout.setBottom(fftChart);  // FFT graph at the bottom

        // Setup and display the scene
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FilterView");
        primaryStage.show();
    }

    /**
     * Updates the time-domain graph with new data and applies the selected filter.
     * <p>
     * This method adds the current speed value (from the slider or oscillation)
     * to the graph and applies either the raised cosine or low-pass filter to 
     * the recent samples. It ensures that both the original and filtered graphs 
     * stay within the window size, removing old data as new points are added.
     * </p>
     *
     * @param speed The current speed value to be plotted on the graph.
     */
    private void updateGraph(double speed) {
        // Add the raw speed data to the original series
        series.getData().add(new XYChart.Data<>(time * X_INTERVAL, speed));
        recentSamples.add(speed);  // Keep track of recent samples

        // Remove oldest sample if the list exceeds the filter length
        if (recentSamples.size() > FILTER_LENGTH) {
            recentSamples.remove(0);
        }

        // Apply either the raised cosine filter or the low-pass filter
        double filteredValue;
        if (useRaisedCosine) {
            double[] raisedCosineCoeffs = FilterUtils.raisedCosineCoefficients(FILTER_LENGTH, beta);
            filteredValue = FilterUtils.applyRaisedCosineFilter(raisedCosineCoeffs, recentSamples);
        } else {
            double[] lowPassCoeffs = FilterUtils.lowPassCoefficients(FILTER_LENGTH, cutoffFrequency);
            filteredValue = FilterUtils.applyLowPassFilter(lowPassCoeffs, recentSamples);
        }

        // Add the filtered speed data to the filtered series
        filteredSeries.getData().add(new XYChart.Data<>(time * X_INTERVAL, filteredValue));
        recentFilteredSamples.add(filteredValue);  // Track recent filtered samples

        // Keep the filtered sample list within the filter length
        if (recentFilteredSamples.size() > FILTER_LENGTH) {
            recentFilteredSamples.remove(0);
        }

        // Ensure both graphs (original and filtered) stay within the window size
        if (series.getData().size() > WINDOW_SIZE) {
            series.getData().remove(0);
            filteredSeries.getData().remove(0);
        }

        // Update the x-axis to scroll as new data is added
        NumberAxis xAxis = (NumberAxis) series.getChart().getXAxis();
        xAxis.setLowerBound((time - WINDOW_SIZE) * X_INTERVAL);
        xAxis.setUpperBound(time * X_INTERVAL);

        // Increment the time step for the next data point
        time++;
    }

    /**
     * Updates the frequency-domain (FFT) graph for both unfiltered and filtered data.
     * <p>
     * This method calculates the FFT (Fast Fourier Transform) of the most recent
     * samples (both original and filtered), computes the magnitudes, shifts the 
     * frequencies to center zero, and updates the FFT graph for both data sets.
     * </p>
     * <p>
     * The FFT graph shows how the frequency content of the signal changes over time.
     * It is recalculated and updated whenever the time-domain data is changed or filtered.
     * </p>
     */
    private void updateFFT() {
        // Calculate the FFT for the unfiltered and filtered data, which returns complex values
        Complex[] unfilteredFFT = FFTUtils.calculateFFT(recentSamples, FFT_SIZE);
        Complex[] filteredFFT = FFTUtils.calculateFFT(recentFilteredSamples, FFT_SIZE);

        // Convert the complex FFT values to magnitudes (absolute values)
        double[] unfilteredMagnitudes = calculateMagnitudes(unfilteredFFT);
        double[] filteredMagnitudes = calculateMagnitudes(filteredFFT);

        // Shift the FFT output to center the zero frequency (DC component) in the middle of the graph
        double[] shiftedUnfilteredMagnitudes = shiftFFT(unfilteredMagnitudes);
        double[] shiftedFilteredMagnitudes = shiftFFT(filteredMagnitudes);

        // Filter out any invalid values such as NaN or Infinity from the magnitude arrays
        shiftedUnfilteredMagnitudes = filterInvalidValues(shiftedUnfilteredMagnitudes);
        shiftedFilteredMagnitudes = filterInvalidValues(shiftedFilteredMagnitudes);

        // Clear and update the FFT graph for the unfiltered signal
        fftSeriesUnfiltered.getData().clear();
        for (int i = 0; i < shiftedUnfilteredMagnitudes.length; i++) {
            fftSeriesUnfiltered.getData().add(new XYChart.Data<>(i - FFT_SIZE / 2, shiftedUnfilteredMagnitudes[i]));
        }

        // Clear and update the FFT graph for the filtered signal
        fftSeriesFiltered.getData().clear();
        for (int i = 0; i < shiftedFilteredMagnitudes.length; i++) {
            fftSeriesFiltered.getData().add(new XYChart.Data<>(i - FFT_SIZE / 2, shiftedFilteredMagnitudes[i]));
        }
    }

    /**
     * Shifts the FFT result so that zero frequency (DC component) is in the center of the graph.
     * <p>
     * FFT results place the zero frequency at the start, followed by positive and negative frequencies. 
     * This method rearranges the result so that the negative frequencies appear on the left side 
     * and positive frequencies appear on the right side, with zero frequency centered.
     * </p>
     * 
     * @param fftData The FFT result (magnitude or complex) to be shifted
     * @return A shifted array with the zero frequency centered
     */
    private double[] shiftFFT(double[] fftData) {
        int n = fftData.length;
        double[] shifted = new double[n];
        int halfSize = n / 2;

        // Move negative frequencies to the beginning of the array
        System.arraycopy(fftData, halfSize, shifted, 0, halfSize);  // Negative frequencies

        // Move positive frequencies to the end of the array
        System.arraycopy(fftData, 0, shifted, halfSize, halfSize);  // Positive frequencies

        return shifted;
    }

    /**
     * Calculates the magnitudes of complex FFT data.
     * <p>
     * FFT results are complex numbers, which represent both amplitude and phase.
     * The magnitude (absolute value) of a complex number represents the strength of each frequency component.
     * </p>
     * 
     * @param fftData The complex FFT data
     * @return An array of magnitudes representing the strength of each frequency component
     */
    private double[] calculateMagnitudes(Complex[] fftData) {
        double[] magnitudes = new double[fftData.length];

        // Loop through FFT results and calculate the magnitude (absolute value) for each complex number
        for (int i = 0; i < fftData.length; i++) {
            magnitudes[i] = fftData[i].abs();  // Magnitude = absolute value of the complex number
        }

        return magnitudes;
    }

    /**
     * Filters out invalid values from a data array.
     * <p>
     * Sometimes the FFT or other calculations can produce invalid results such as NaN (Not a Number)
     * or Infinity. This method replaces such values with zero to prevent display or calculation issues.
     * </p>
     * 
     * @param data The array of data to be filtered
     * @return A cleaned array with invalid values replaced by zero
     */
    private double[] filterInvalidValues(double[] data) {
        // Loop through the data array
        for (int i = 0; i < data.length; i++) {
            // Check if the value is NaN (Not a Number) or Infinite and replace it with zero
            if (Double.isNaN(data[i]) || Double.isInfinite(data[i])) {
                data[i] = 0; // Replace invalid values with 0
            }
        }

        return data;
    }

    /**
     * The main method to launch the JavaFX application.
     * <p>
     * This method is the entry point for launching the JavaFX GUI application.
     * It sets up the window and calls the {@code start} method to initialize the interface.
     * </p>
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application
    }
}
