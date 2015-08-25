/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Marc
 */
public class IngestorTypes {

    private List<String> names = new ArrayList<String>();

    public IngestorTypes (String...nams) {
        for (String n : nams) {
            names.add(n);
        }
    }

    public List<String> getNames () {
        return Collections.<String>unmodifiableList(names);
    }
}
