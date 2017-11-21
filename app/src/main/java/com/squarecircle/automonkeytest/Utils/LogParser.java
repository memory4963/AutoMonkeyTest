package com.squarecircle.automonkeytest.Utils;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by mufengjun260 on 17-11-4.
 */

public class LogParser {
    private Vector<String> exceptionVector = new Vector<>();
    private Vector<String> eventVector = new Vector<>();
    private Vector<String> notUsingVector = new Vector<>();
    private Vector<String> dropedVector = new Vector<>();

    public LogParser(String inputs) throws IOException {
        String line;

        String inputsArray[] = inputs.split("\n");
        for (int i = 0; i <= inputsArray.length - 1; i++) {
            line = inputsArray[i];
            if (line.contains("Got")) {
                exceptionVector.add(line.substring(28, line.length()));
            }
            if (line.contains("Events injected")) {
                eventVector.add(line.substring(17, line.length()));
            }
            if (line.contains(":Dropped")) {
                dropedVector.add(line);
            }
            if (line.contains("//   - NOT USING main activity")) {
                notUsingVector.add(line.substring(31, line.length()));
            }
        }
    }

    public String vectorToString(Vector<String> vector) {
        String result = "";
        for (int i = 0; i <= vector.size() - 1; i++) {
            result += vector.get(i) + '\n';
        }
        return result;
    }

    public Vector<String> getDropedVector() {
        return dropedVector;
    }

    public Vector<String> getEventVector() {
        return eventVector;
    }

    public Vector<String> getExceptionVector() {
        return exceptionVector;
    }

    public Vector<String> getNotUsingVector() {
        return notUsingVector;
    }
}
