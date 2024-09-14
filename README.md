# Speed Graph with Adjustable Raised Cosine Filter

This JavaFX application demonstrates the concept of a raised cosine filter applied to signal data, visualized in a dynamic graph. The user can control two key parameters:

## Key Features

### 1. Speed (Vertical Slider)
- Controlled via a vertical slider.
- This parameter determines the value of the signal (represented as speed) at each time step.
- The slider's range is from -100 to 100.

### 2. Beta (Roll-off Factor - Horizontal Slider)
- Controlled via a horizontal slider.
- Adjusts the roll-off factor (β) of the raised cosine filter.
- A higher beta value produces smoother transitions in the signal.
- The range is from 0 (sharp transitions) to 1 (smooth transitions).

## Graph Display
The graph displays two series:
- **Original Speed**: Represents the unfiltered signal, directly derived from the speed slider.
- **Filtered Speed (Raised Cosine)**: Represents the signal after it has been processed by a raised cosine filter, smoothing transitions based on the roll-off factor (beta).

### Dynamic Graph
- The graph updates in real-time, continuously adding new points. 
- It provides a sliding window effect, always showing the latest data over a fixed time window.
  
### Pre-populated Graph
- The graph is filled with zeros at startup, ensuring smoother transitions immediately.

### Adjustable Raised Cosine Filter
- The filtered speed is processed using a raised cosine filter.
- The filter is adjustable via the beta slider to simulate smooth signal transitions.

### Signal Smoothing with FIR Filtering
- The raised cosine filter is implemented as a finite impulse response (FIR) filter.
- The coefficients (taps) of the filter are dynamically generated and applied to the input signal for smoothing.

### Live Beta Value Display
- The current beta value is displayed dynamically as the slider is adjusted.
- This helps the user visualize the filter’s effect in real-time.

## Technologies Used
- **JavaFX**: For the graphical user interface, including sliders and real-time graph.
- **JFreeChart**: For rendering the dynamic line chart visualizing signal data.

## Purpose
This application simulates how a raised cosine filter can smooth transitions in a signal, with user-controllable parameters. It's particularly relevant for digital communication systems, where pulse shaping is critical for minimizing bandwidth and reducing inter-symbol interference (ISI).
