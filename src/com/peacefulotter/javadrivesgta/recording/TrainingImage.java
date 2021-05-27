package com.peacefulotter.javadrivesgta.recording;

import java.awt.image.BufferedImage;

public class TrainingImage
{
    private BufferedImage image;
    private int acceleration, direction;

    public BufferedImage getImage()
    {
        return image;
    }

    public void setImage( BufferedImage image )
    {
        this.image = image;
    }

    public int getAcceleration()
    {
        return acceleration;
    }

    public void setAcceleration( int acceleration )
    {
        this.acceleration = acceleration;
    }

    public int getDirection()
    {
        return direction;
    }

    public void setDirection( int direction )
    {
        this.direction = direction;
    }
}
