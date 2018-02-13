package main.java.org.manoilok.speachrecognition.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Modest on 09.12.2016.
 */
public class ActivityDetector {

    private double                thresholdLevel;
    private int                  minActivityLen;
    private int                  minSilenceLen;

    private List<Double>              signalEnvelope;
    private int                  pos;                          // current position in signal
    private List<Integer>        activeZones;

    /**
     * Creates an activity detector.
     *
     * @param thresholdLevel
     *    The threshold signal envelope level to distinguish activity from silence.
     * @param minActivityLen
     *    The minimum number of samples for an "active" segment.
     * @param minSilenceLen
     *    The minimum number of samples for a "silence" segment.
     */
    public ActivityDetector (double thresholdLevel, int minActivityLen, int minSilenceLen) {
        this.thresholdLevel = thresholdLevel;
        this.minActivityLen = minActivityLen;
        this.minSilenceLen = minSilenceLen; }

    /**
     * Processes the signal envelope and returns the positions of the active zones.
     *
     * @param signalEnvelope
     *    The envelope of the input signal.
     * @return
     *    An array with the start and end positions of the active zones.
     *    The size of the array is twice the number of active zones.
     *    The sequence is: active1Start, active1End, active2Start, active2End, ...
     */
    public List<Integer> process (List<Double> signalEnvelope) {
        this.signalEnvelope = signalEnvelope;
        pos = 0;
        activeZones = new ArrayList<>(32);
        int activeStartPos = -1;                                // start position of active zone or -1
        int undefStartPos = -1;                                 // start position of undefined zone or -1
        while (pos < signalEnvelope.size()) {
            int segmentStartPos = pos;                           // start position of current segment
            SegmentType segmentType = scanSegment();
            switch (segmentType) {
                case silence: {
                    if (activeStartPos != -1) {
                        addActiveZone(activeStartPos, segmentStartPos);
                        activeStartPos = -1; }
                    undefStartPos = -1;
                    break; }
                case active: {
                    if (activeStartPos == -1) {
                        activeStartPos = (undefStartPos != -1) ? undefStartPos : segmentStartPos; }
                    break; }
                case undef: {
                    if (undefStartPos == -1) {
                        undefStartPos = segmentStartPos; }
                    break; }
                default:
                    throw new AssertionError(); }}
        if (activeStartPos != -1) {
            addActiveZone(activeStartPos, pos); }
        return activeZones; }

    private enum SegmentType { active, silence, undef }

    private SegmentType scanSegment() {
        int startPos = pos;
        if (pos >= signalEnvelope.size()) {
            throw new AssertionError(); }
        boolean active = signalEnvelope.get(pos++) >= thresholdLevel;
        while (pos < signalEnvelope.size() && (signalEnvelope.get(pos) >= thresholdLevel) == active) {
            pos++; }
        int minLen = active ? minActivityLen : minSilenceLen;
        if (pos - startPos < minLen) {
            return SegmentType.undef; }
        return active ? SegmentType.active : SegmentType.silence; }

    private void addActiveZone (int startPos, int endPos) {
        activeZones.add(startPos);
        activeZones.add(endPos); }

}

