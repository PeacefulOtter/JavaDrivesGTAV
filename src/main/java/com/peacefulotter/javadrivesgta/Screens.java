package com.peacefulotter.javadrivesgta;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class Screens extends GridPane
{
    private final List<MultiRenderer> multiRenderers = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();

    public void render()
    {
        for ( MultiRenderer mr: multiRenderers )
            mr.render();

        for ( Renderer r: renderers )
            r.setImage( r.render() );
    }

    public void addMultiRenderer( MultiRenderer mr )
    {
        multiRenderers.add( mr );
    }

    public void addRenderer( Renderer r )
    {
        renderers.add( r );
        getChildren().add( r );
    }
}
