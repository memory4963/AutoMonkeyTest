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
    private Vector<IntentSaver> intentSaverVector = new Vector<>();
    private Vector<String> precentageOfEvent = new Vector<>();
    private String seed, count, switchString;

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
            if (line.contains(":Monkey: seed=")) {
                seed = line.substring(14, line.indexOf("count="));
                count = line.substring(line.indexOf("count=") + 6, line.length());
            }
            if (line.contains(":Switch: ")) {
                switchString = line.substring(9, line.length() - 3);
            }
            if (line.contains("    // Allowing start of Intent { ")) {
                IntentSaver intentSaver = new IntentSaver();
                intentSaver.setAct(line.substring(line.indexOf("act=") + 4, line.indexOf("cat")));
                intentSaver.setCat(line.substring(line.indexOf("cat=") + 5, line.indexOf("cmp") - 2));
                intentSaver.setCmp(line.substring(line.indexOf("cmp=") + 4, line.indexOf(" } in package")));
                intentSaver.setPkg(line.substring(line.indexOf(" } in package") + 13, line.length()));
                intentSaverVector.add(intentSaver);
            }
            for (int tmpInt = 0; tmpInt <= 11; tmpInt++) {
                String signToken = "//   " + tmpInt + ": ";
                if (line.contains(signToken)) {
                    precentageOfEvent.add(line.substring(signToken.length(), line.length()));
                }
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

    public String getSeed() {
        return seed;
    }

    public String getCount() {
        return count;
    }

    public Vector<IntentSaver> getIntentSaverVector() {
        return intentSaverVector;
    }

    public Vector<String> getDropedVector() {
        return dropedVector;
    }

    public Vector<String> getPrecentageOfEvent() {
        return precentageOfEvent;
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
