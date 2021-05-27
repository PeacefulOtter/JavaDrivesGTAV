package com.peacefulotter.javadrivesgta.io;

import net.java.games.input.*;

/**
 * Acceleration and direction are between 0 and 200, 100 means no input
 *
 */
public class ControllerHandler extends IOHandler
{
    private static final int NO_INPUT_VALUE = 100;
    private static final int JOYSTICK_DEAD_ZONE = 10;
    private static final int TRIGGER_DEAD_ZONE = 5;

    private static Controller controller;

    public void init()
    {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for ( Controller c : controllers )
        {
            if ( c.getType() == Controller.Type.GAMEPAD )
            {
                System.out.println( "Found Controller" );
                ControllerHandler.controller = c;
            }
        }
    }

    public void pollInput()
    {
        if ( controller == null )
            return;

        EventQueue queue = controller.getEventQueue();
        Event event = new Event();

        // Polls axes for data. Returns false if the controller is no longer valid.
        // Polling reflects the current state of the device when polled.
        if ( !controller.poll() )
            throw new UnsupportedOperationException( "Controller cannot poll axis data" );

        queue.getNextEvent( event );

        Component component = event.getComponent();

        if ( component == null ) return;

        Component.Identifier identifier = component.getIdentifier();
        int roundedXValue = Math.round( event.getValue() * 100 );

        if ( identifier.getName().equals( "z" ) )
        {
            if ( Math.abs( roundedXValue ) > TRIGGER_DEAD_ZONE )
                setAcceleration( -roundedXValue + NO_INPUT_VALUE );
            else
                setAcceleration( NO_INPUT_VALUE );
        }
        else if ( identifier.getName().equals( "x" ) )
        {
            if ( Math.abs( roundedXValue ) > JOYSTICK_DEAD_ZONE )
                setDirection( roundedXValue + NO_INPUT_VALUE );
            else
                setDirection( NO_INPUT_VALUE );
        }

        System.out.println("Controller: dir: " + getDirection() + ", acc: " + getAcceleration());
    }
}
