package org.opentripplanner.graph_builder.module.map;

import java.util.Iterator;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.linearref.LinearLocation;

/**
 * I copied this class from JTS but made a few changes.
 * 
 * The JTS version of this class has several design decisions that don't work for me. In particular,
 * hasNext() in the original should be "isValid", and if we start mid-segment, we should continue at
 * the end of this segment rather than the end of the next segment.
 */
public class LinearIterator implements Iterable<LinearLocation> {

    private Geometry linearGeom;

    private final int numLines;

    /**
     * Invariant: currentLine <> null if the iterator is pointing at a valid coordinate
     * 
     * @throws IllegalArgumentException if linearGeom is not lineal
     */
    private LineString currentLine;

    private int componentIndex = 0;

    private int vertexIndex = 0;

    private double segmentFraction;

    /**
     * Creates an iterator starting at a {@link LinearLocation} on a linear {@link Geometry}
     * 
     * @param linear the linear geometry to iterate over
     * @param start the location to start at
     * @throws IllegalArgumentException if linearGeom is not lineal
     */
    public LinearIterator(Geometry linear, LinearLocation start) {
        this(linear, start.getComponentIndex(), start.getSegmentIndex());
        this.segmentFraction = start.getSegmentFraction();
    }

    /**
     * Creates an iterator starting at a specified component and vertex in a linear {@link Geometry}
     * 
     * @param linearGeom the linear geometry to iterate over
     * @param componentIndex the component to start at
     * @param vertexIndex the vertex to start at
     * @throws IllegalArgumentException if linearGeom is not lineal
     */
    public LinearIterator(Geometry linearGeom, int componentIndex, int vertexIndex) {
        if (!(linearGeom instanceof Lineal))
            throw new IllegalArgumentException("Lineal geometry is required");
        this.linearGeom = linearGeom;
        numLines = linearGeom.getNumGeometries();
        this.componentIndex = componentIndex;
        this.vertexIndex = vertexIndex;
        loadCurrentLine();
    }

    private void loadCurrentLine() {
        if (componentIndex >= numLines) {
            currentLine = null;
            return;
        }
        currentLine = (LineString) linearGeom.getGeometryN(componentIndex);
    }

    /**
     * Tests whether there are any vertices left to iterator over.
     * 
     * @return <code>true</code> if there are more vertices to scan
     */
    public boolean hasNext() {
        if (componentIndex >= numLines)
            return false;
        if (componentIndex == numLines - 1 && vertexIndex >= currentLine.getNumPoints() - 1)
            return false;
        return true;
    }

    /**
     * Moves the iterator ahead to the next vertex and (possibly) linear component.
     */
    public void next() {
        if (!hasNext())
            return;
        segmentFraction = 0.0;
        vertexIndex++;
        if (vertexIndex >= currentLine.getNumPoints()) {
            componentIndex++;
            if (componentIndex < linearGeom.getNumGeometries() - 1) {
                loadCurrentLine();
                vertexIndex = 0;
            }
        }
    }

    public LinearLocation getLocation() {
        return new LinearLocation(componentIndex, vertexIndex, segmentFraction);
    }

    class LinearIteratorIterator implements Iterator<LinearLocation> {

        @Override
        public boolean hasNext() {
            return LinearIterator.this.hasNext();
        }

        @Override
        public LinearLocation next() {
            LinearLocation result = getLocation();
            LinearIterator.this.next();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    @Override
    public Iterator<LinearLocation> iterator() {
        return new LinearIteratorIterator();
    }
}