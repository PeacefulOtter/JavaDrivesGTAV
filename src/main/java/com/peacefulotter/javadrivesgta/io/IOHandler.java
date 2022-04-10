package com.peacefulotter.javadrivesgta.io;

public abstract class IOHandler
{
    private int direction, acceleration;

    public abstract void init();
    public abstract void pollInput();

    protected void setDirection( int direction ) { this.direction = direction; }
    protected void setAcceleration( int acceleration ) { this.acceleration = acceleration; }

    public int getDirection() { return direction; }
    public int getAcceleration() { return acceleration; }
}
