package com.peacefulotter.javadrivesgta.utils;

public class Time
{
    public static final long SECOND = (long) Math.pow( 10, 9 );

    public static double getNanoTime() { return System.nanoTime(); }
}