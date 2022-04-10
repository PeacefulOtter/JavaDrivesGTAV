package com.peacefulotter.javadrivesgta.utils;

public class Settings
{
    public static final int CAPTURE_WIDTH = 100;
    public static final int CAPTURE_HEIGHT = 75;
    public static final int TOP_LEFT_X = 500;
    public static final int TOP_LEFT_Y = 650;

    public static final boolean POLL_KEYBOARD = true;
    public static final boolean POLL_CONTROLLER = !POLL_KEYBOARD;
    public static final boolean RECORD_CAPTURE = false;

    public static final int MAX_IMAGES = 255; // MAX VALUE = 255
    public static final int CAPTURE_FREQUENCY = 3;

    public static final double LEARNING_RATE = 0.005;
    public static final int EPOCHS = 250;
    public static final int BATCH_SIZE = 1;
    public static final int PRINT_PERIOD = 10;
}
