/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.logging;

/**
 *
 * @author Marc
 */
public class CountRangePolicy implements IngestorPolicy {

    private int startAt = -1;
    private int stopAt = Integer.MAX_VALUE;
    private boolean doThrow;
    private int count;

    public CountRangePolicy (int start, int stop, boolean doThrow) {
        startAt = start;
        stopAt = stop;
        this.doThrow = doThrow;
    }

    @Override
    public void ate(Object obj) {
        if (obj instanceof Integer) {
            count = (Integer)obj;
        }
    }

    @Override
    public boolean shouldStart() {
        return count >= startAt && count <= stopAt;
    }

    @Override
    public boolean shouldStop() {
        return count >= stopAt;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }

    @Override
    public boolean shouldThrow() {
        return shouldStop() && doThrow;
    }

}
