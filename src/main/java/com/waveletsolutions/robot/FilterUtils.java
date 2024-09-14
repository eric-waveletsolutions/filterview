package com.waveletsolutions.robot;

import java.util.List;

/**
 * The {@code FilterUtils} class provides utility methods for creating and applying
 * digital filters, such as raised cosine filters and low-pass filters.
 * <p>
 * These filters are commonly used in signal processing to smooth or shape signals.
 * The class offers methods to generate filter coefficients and apply them to a set 
 * of samples. The filters implemented here are designed for basic signal processing 
 * tasks.
 * </p>
 * 
 * <h2>Filters Implemented:</h2>
 * <ul>
 *   <li>Raised Cosine Filter: Used for shaping signals with controlled bandwidth.</li>
 *   <li>Low-Pass Filter: Allows signals below a certain frequency to pass while attenuating higher frequencies.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <ul>
 *   <li>Use the {@code raisedCosineCoefficients} or {@code lowPassCoefficients} to generate filter coefficients.</li>
 *   <li>Apply the generated filter to a set of samples using {@code applyRaisedCosineFilter} or {@code applyLowPassFilter}.</li>
 * </ul>
 * 
 * @author Eric Ratliff
 * @version 1.0.0
 * @since 2024-09-09
 */
public class FilterUtils {

    /**
     * Generates raised cosine filter coefficients.
     * <p>
     * The raised cosine filter is used for pulse shaping in communication systems. 
     * It shapes the signal to control the bandwidth while reducing intersymbol interference. 
     * The filter is defined by a roll-off factor {@code beta}, which controls how much excess 
     * bandwidth is allowed.
     * </p>
     * 
     * @param length the number of filter coefficients (the length of the filter)
     * @param beta the roll-off factor (ranges between 0 and 1)
     * @return an array of raised cosine filter coefficients
     */
    public static double[] raisedCosineCoefficients(int length, double beta) {
        double[] coeffs = new double[length];
        double sum = 0.0; 

        for (int i = 0; i < length; i++) {
            double t = (double) i / (length - 1);
            double cosPart = 0.5 * (1 + Math.cos(Math.PI * (2 * t - 1) * beta));
            coeffs[i] = cosPart;
            sum += cosPart;
        }

        // Normalize the coefficients to ensure proper filtering
        for (int i = 0; i < length; i++) {
            coeffs[i] /= sum;
        }

        return coeffs;
    }

    /**
     * Applies the raised cosine filter to a list of samples.
     * <p>
     * This method multiplies the given raised cosine filter coefficients with 
     * the most recent samples to smooth the signal.
     * </p>
     * 
     * @param coeffs the raised cosine filter coefficients
     * @param samples the time-domain samples to be filtered
     * @return the filtered value of the signal at the current time step
     */
    public static double applyRaisedCosineFilter(double[] coeffs, List<Double> samples) {
        double result = 0;
        for (int i = 0; i < coeffs.length; i++) {
            result += coeffs[i] * samples.get(samples.size() - 1 - i);
        }
        return result;
    }

    /**
     * Generates low-pass filter coefficients using a Hamming window.
     * <p>
     * This method creates a low-pass filter, which allows frequencies below a certain 
     * cutoff to pass through while attenuating higher frequencies. The filter is generated 
     * using a Hamming window for smooth transitions.
     * </p>
     * 
     * @param length the number of filter coefficients (the length of the filter)
     * @param cutoffFrequency the cutoff frequency as a fraction of the sampling rate (e.g., 0.1 for 10%)
     * @return an array of low-pass filter coefficients
     */
    public static double[] lowPassCoefficients(int length, double cutoffFrequency) {
        double[] coeffs = new double[length];
        double sum = 0.0;

        for (int i = 0; i < length; i++) {
            double t = i - (length - 1) / 2.0;
            if (t == 0.0) {
                coeffs[i] = 2 * cutoffFrequency;
            } else {
                coeffs[i] = Math.sin(2 * Math.PI * cutoffFrequency * t) / (Math.PI * t);
            }
            coeffs[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (length - 1));  // Apply Hamming window
            sum += coeffs[i];
        }

        // Normalize the coefficients to ensure proper filtering
        for (int i = 0; i < length; i++) {
            coeffs[i] /= sum;
        }

        return coeffs;
    }

    /**
     * Applies a low-pass filter to a list of samples.
     * <p>
     * This method multiplies the given low-pass filter coefficients with 
     * the most recent samples to smooth the signal and reduce high-frequency noise.
     * </p>
     * 
     * @param coeffs the low-pass filter coefficients
     * @param samples the time-domain samples to be filtered
     * @return the filtered value of the signal at the current time step
     */
    public static double applyLowPassFilter(double[] coeffs, List<Double> samples) {
        double result = 0;
        for (int i = 0; i < coeffs.length; i++) {
            result += coeffs[i] * samples.get(samples.size() - 1 - i);
        }
        return result;
    }
}
