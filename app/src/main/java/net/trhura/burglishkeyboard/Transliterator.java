package net.trhura.burglishkeyboard;

import android.content.Context;
import android.util.Log;

import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by trhura on 11/2/14.
 */
public class Transliterator {

    private static final int MAXCANDIDATES = 15;
    private static Transliterator instance = null;
    private static final String TAG = "Burglish";

    private Trie<String, String> prefixTrie;

    private Transliterator (InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        prefixTrie = new PatriciaTrie<String, String>(StringKeyAnalyzer.CHAR);
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                String trimmedLine = line.trim();
                List<String> items = Arrays.asList(trimmedLine.split(","));
                if (items.size() < 2) continue;

                prefixTrie.put(items.get(0), items.get(1));
            }

            Log.d(TAG, "Loaded transliteraion resource successfully.");
            inputStream.close();

        } catch (IOException e) {
            Log.e(TAG, "Cannot load transliteraion resource");
        }
    }

    public String getBurglish (String text) {
        String mText = text.toLowerCase();
        Log.d(TAG, "burglish for "  + mText + " is " + prefixTrie.get(mText));
        return prefixTrie.get(text);
    }

    public List<String> getBurglishCandidates (String text) {
        String mText = text.toLowerCase();
        ArrayList<String> candidates = new ArrayList<String>();
        SortedMap<String, String> matches = prefixTrie.prefixMap(mText);

        int i = 0;
        for (Map.Entry<String, String> match: matches.entrySet()) {
            Log.d(TAG, i + "adding " + match.getValue());
            if (i++ >= MAXCANDIDATES) break;
            candidates.add(match.getValue());
        }

        return candidates;
    }

    public static Transliterator getInstance (Context context) {
        if (instance != null) return instance;

        InputStream inputStream = context.getResources().openRawResource(R.raw.transliteration);
        instance = new Transliterator(inputStream);
        return instance;
    }

}
