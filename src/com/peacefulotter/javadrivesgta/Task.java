package com.peacefulotter.javadrivesgta;

/**
 * Represents something that the GameLoop will do every frame
 * for instance recording (Recording class) or getting the prediction from the CNN (NNManager class)
 */
public abstract class Task
{
    public abstract void action( int acc, int dir );
}
