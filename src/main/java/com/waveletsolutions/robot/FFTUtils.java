package com.waveletsolutions.robot;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.List;

/**
 * The {@code FFTUtils} class provides utility methods for performing 
 * Fast Fourier Transform (FFT) operations on time-domain signals.
 * <p>
 * This class is used to convert a list of time-domain samples into their 
 * frequency-domain representation using the FFT. The primary method is 
 * designed to handle input of varying sizes by either padding or truncating 
 * the input data to fit the required FFT size.
 * </p>
 * 
 * <h2>Usage:</h2>
 * <ul>
 *   <li>Call the {@code calculateFFT} method with a list of time-domain samples and the desired FFT size.</li>
 *   <li>The method returns an array of {@code Complex} numbers representing the frequency components of the signal.</li>
 * </ul>
 * 
 * <h2>Technical Concepts:</h2>
 * <ul>
 *   <li>Fast Fourier Transform (FFT): Converts time-domain signals to frequency-domain signals.</li>
 *   <li>Padding: If the number of input samples is smaller than the required FFT size, the input is padded with zeros.</li>
 *   <li>Truncation: If the number of input samples exceeds the FFT size, the input is truncated.</li>
 * </ul>
 * 
 * @author Eric Ratliff
 * @version 1.0.0
 * @since 2024-09-09
 */
public class FFTUtils {
    /**
     * Calculates the Fast Fourier Transform (FFT) of a list of time-domain samples.
     * <p>
     * This method takes in a list of time-domain samples, applies padding or truncation 
     * to ensure the correct FFT size, and then computes the FFT. The result is returned 
     * as an array of {@code Complex} numbers representing the frequency-domain data.
     * </p>
     * 
     * @param samples the list of time-domain samples to be transformed
     * @param fftSize the desired size of the FFT (the number of points in the frequency domain)
     * @return an array of {@code Complex} numbers representing the frequency components of the input signal
     * 
     * <ul>
     *   <li>If the number of samples is less than {@code fftSize}, the input array is padded with zeros.</li>
     *   <li>If the number of samples exceeds {@code fftSize}, the input array is truncated.</li>
     *   <li>The FFT is computed using the Apache Commons Math {@code FastFourierTransformer} class.</li>
     * </ul>
     */
    public static Complex[] calculateFFT(List<Double> samples, int fftSize) {
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        double[] inputArray = samples.stream().mapToDouble(Double::doubleValue).toArray();

        // Ensure that the input array has the right size
        if (inputArray.length < fftSize) {
            inputArray = java.util.Arrays.copyOf(inputArray, fftSize); // Pad with zeros if too small
        } else if (inputArray.length > fftSize) {
            inputArray = java.util.Arrays.copyOfRange(inputArray, 0, fftSize); // Truncate if too large
        }

        return transformer.transform(inputArray, TransformType.FORWARD);
    }
}
