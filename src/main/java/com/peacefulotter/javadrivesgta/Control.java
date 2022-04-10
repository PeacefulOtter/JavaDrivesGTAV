package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.ControllerHandler;
import com.peacefulotter.javadrivesgta.io.IOHandler;
import com.peacefulotter.javadrivesgta.io.KeyboardHandler;
import com.peacefulotter.javadrivesgta.utils.Settings;

public class Control {
    // INPUT HANDLERS
    public static final IOHandler HANDLER = Settings.POLL_KEYBOARD
            ? new KeyboardHandler()
            : new ControllerHandler();
}
