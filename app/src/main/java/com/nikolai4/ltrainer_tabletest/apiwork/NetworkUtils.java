package com.nikolai4.ltrainer_tabletest.apiwork;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

// dict.1.1.20230827T095647Z.a0a3889d81f27a84.e4b7f10161413ef6b5070e9562c2a7a628cf9b5c - yandex key
public class NetworkUtils {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String ACCEPT_HEADER_KEY = "Accept";
    private static final String ACCEPT_HEADER_VALUE = "application/json";
    private static final String APP_ID_KEY = "app_id";
    private static final String APP_KEY_KEY = "app_key";
    private static final String APP_KEY_VALUE = "2e1d4cb196137c3cf46db966cdfd39af";
    private static final String APP_ID_VALUE = "3fcb5adf";
    private static final String BASE_URL = "https://od-api.oxforddictionaries.com/api/v2/entries/en-us/";
    private static final String ENDPOINT = "entries";
    private static final String LANGUAGE_CODE = "en-us";
    private static String WORD_ID;

    private static String mAudioLink = "";

    public void setWordId(String word) {
        WORD_ID = word;
    }

    // try: if (future.isDone() { infoList = future.get() }
    public static List<String> startExecutionTask(String word) {
//        List<String> infoList = null;
//        Future<List<String>> future = executor.submit(new Callable<List<String>>() {
//            @Override
//            public List<String> call() throws Exception {
                String stringJSONObject = getStringJSONObject(word);
                return getInfoFromJSON(stringJSONObject);
//            }
//        });
//        try {
//            infoList = future.get();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return infoList;
    }

    public static String startAudioTask(String word) {
        String audioLink = "";
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String stringJSONObject = getStringJSONObject(word);
                return getAudioLink(stringJSONObject);
            }
        });
        try {
            audioLink = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return audioLink;

//        try {
//            String stringJSONObject = getStringJSONObject(word);
//            return getAudioLink(stringJSONObject);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            return "";
//        }
    }

    public static String startTranscriptionTask(String word) {
        String transcription = "";
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String stringJSONObject = getStringJSONObject(word);
                return getTranscription(stringJSONObject);
            }
        });
        try {
            transcription = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return transcription;
    }


    // don't forget "strictMatch=false"
    private static String getStringJSONObject(String word) {
        // i need to get: transcription, synonyms, definition(?), examples(?), pronunciation (link to mp3)
        String JSONString = "";
        HttpsURLConnection connection = null;
        BufferedReader bufferedReader = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(word.toLowerCase())
//                .appendQueryParameter("entries", ENDPOINT)
//                .appendQueryParameter("language_code", LANGUAGE_CODE)
//                .appendQueryParameter("word_id", word.toLowerCase())
                .build();

        try {
            URL url = new URL(uri.toString());
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE);
            connection.setRequestProperty(APP_ID_KEY, APP_ID_VALUE);
            connection.setRequestProperty(APP_KEY_KEY, APP_KEY_VALUE);
            connection.setRequestMethod("GET");
            connection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            JSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.d("JSONString", "getStringJSONObject: " + JSONString);
        return JSONString;
    }

    private static List<String> getInfoFromJSON(String JSONString) {
        List<String> entriesList = new ArrayList<>();
        StringBuilder wordInfo = new StringBuilder();

        String allLexicalCategoriesLabel = "lexical categories:\n";
        String synonymsLabel = "synonyms:\n";
        String examplesLabel = "examples:\n";
        StringBuilder lexicalCategoryString = new StringBuilder("");
        String transcription = "";
        StringBuilder exampleString = new StringBuilder("");
        StringBuilder synonymsString = new StringBuilder("");
        String audioFileString = "";

        JSONObject mainObject = mainObject(JSONString);
        // there could be a few result with their own fields. Let's take only one result.
        JSONArray resultsArray = resultsArray(mainObject);
        JSONObject resultObject = resultObject(resultsArray, 0);
        JSONArray lexicalEntries = lexicalEntries(resultObject);
        // there may be a few lexicalEntries separated by lexical category. We're taking 'em all.
        for (int i = 0; i < lexicalEntries.length(); i++) {
            JSONObject lexicalEntryObject = lexicalEntryObject(lexicalEntries, i);
            JSONObject lexicalCategoryObject = lexicalCategoryObject(lexicalEntryObject);

            lexicalCategoryString.append(lexicalCategoryString(lexicalCategoryObject));
            if (i < lexicalEntries.length()-1) {
                lexicalCategoryString.append(", ");
            }

            JSONArray entries = entries(lexicalEntryObject);
            for (int j = 0; j < entries.length(); j++) {
                JSONObject entryObject = entryObject(entries, j);

                if (transcription.trim().isEmpty()) {
                    JSONArray pronunciations = pronunciations(entryObject);
                    for (int k = 0; k < pronunciations.length(); k++) {
                        JSONObject pronunciationObject = pronunciationObject(pronunciations, k);
                        if (transcription.trim().isEmpty()) {
                            transcription = phoneticSpelling(pronunciationObject);
                        } else {
                            break;
                        }
                    }
                }

                if (audioFileString.trim().isEmpty()) {
                    JSONArray pronunciations = pronunciations(entryObject);
                    for (int k = 0; k < pronunciations.length(); k++) {
                        JSONObject pronunciationObject = pronunciationObject(pronunciations, k);
                        if (audioFileString.trim().isEmpty()) {
                            audioFileString = audioFile(pronunciationObject);
                        } else {
                            break;
                        }
                    }
                }

                JSONArray senses = senses(entryObject);
                for (int k = 0; k < senses.length(); k++) {
                    JSONObject sensesObject = sensesObject(senses, k);

                    JSONArray examples = examples(sensesObject);
                    if (examples.length() > 0) {
                        for (int l = 0; l < examples.length(); l++) {
                            JSONObject examplesObject = examplesObject(examples, l);
                            if (!exampleString.toString().isEmpty()) {
                                exampleString.append("\n");
                            }
                            exampleString.append(exampleString(examplesObject));
                        }
                    }

                    JSONArray synonyms = getSynonyms(sensesObject);
                    if (synonyms.length() > 0) {
                        for (int l = 0; l < synonyms.length(); l++) {
                            JSONObject synonymsObject = synonymsObject(synonyms, l);
                            if (!synonymsString.toString().isEmpty()) {
                                synonymsString.append("\n");
                            }
                            synonymsString.append(synonymString(synonymsObject));
                        }
                    }

                    if (synonymsString.toString().isEmpty()) {
                        synonymsString.append(getSubsensesSynonyms(sensesObject));
                    }
                }
            }
        }

//        wordInfo.append(getWord(mainObject));
//        wordInfo.append("\n");
//        wordInfo.append(transcription);
//        wordInfo.append("\n");
//        wordInfo.append(allLexicalCategoriesLabel);
//        wordInfo.append(lexicalCategoryString);
//        wordInfo.append("\n");
//        wordInfo.append(examplesLabel);
//        wordInfo.append(exampleString);
//        wordInfo.append(synonymsLabel);
//        wordInfo.append(synonymsString);

//        entriesList.add(wordInfo.toString());

        entriesList.add(getWord(mainObject));
        entriesList.add(transcription);
        entriesList.add(lexicalCategoryString.toString());
        entriesList.add(exampleString.toString());
        entriesList.add(synonymsString.toString());

        Log.d("examples", "\nexamples: " + exampleString + "\n");
        Log.d("synonyms", "\nsynonyms: " + synonymsString + "\n");

//        Log.d("JSONtag", "\ngetInfoFromJSON: " + entriesList.get(0) + "\n");
        return entriesList;
    }

    private static String getAudioLink(String JSONString) {
        String link = "";

        JSONObject mainObject = mainObject(JSONString);
        JSONArray resultsArray = resultsArray(mainObject);
        JSONObject resultObject = resultObject(resultsArray, 0);
        JSONArray lexicalEntries = lexicalEntries(resultObject);

        for (int i = 0; i < lexicalEntries.length(); i++) {
            if (link.isEmpty()) {
                JSONObject lexicalEntryObject = lexicalEntryObject(lexicalEntries, i);

                JSONArray entries = entries(lexicalEntryObject);
                for (int j = 0; j < entries.length(); j++) {
                    JSONObject entryObject = entryObject(entries, j);
                    if (link.isEmpty()) {
                        JSONArray pronunciations = pronunciations(entryObject);
                        for (int k = 0; k < pronunciations.length(); k++) {
                            JSONObject pronunciationObject = pronunciationObject(pronunciations, k);
                            if (link.isEmpty()) {
                                link = audioFile(pronunciationObject);
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return link;
    }

    private static String getTranscription(String JSONString) {
        String transcription = "";

        JSONObject mainObject = mainObject(JSONString);
        JSONArray resultsArray = resultsArray(mainObject);
        JSONObject resultObject = resultObject(resultsArray, 0);
        JSONArray lexicalEntries = lexicalEntries(resultObject);

        for (int i = 0; i < lexicalEntries.length(); i++) {
            if (transcription.isEmpty()) {
                JSONObject lexicalEntryObject = lexicalEntryObject(lexicalEntries, i);

                JSONArray entries = entries(lexicalEntryObject);
                for (int j = 0; j < entries.length(); j++) {
                    JSONObject entryObject = entryObject(entries, j);
                    if (transcription.isEmpty()) {
                        JSONArray pronunciations = pronunciations(entryObject);
                        for (int k = 0; k < pronunciations.length(); k++) {
                            JSONObject pronunciationObject = pronunciationObject(pronunciations, k);
                            if (transcription.isEmpty()) {
                                transcription = phoneticSpelling(pronunciationObject);
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return transcription;
    }

    private static String getWord(JSONObject mainObject) {
        try {
            return mainObject.getString("word");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static JSONObject mainObject(String JSONString) {
        try {
            return new JSONObject(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static JSONArray resultsArray(JSONObject mainObject) {
        try {
            return mainObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject resultObject(JSONArray resultsArray, int i) {
        try {
            return resultsArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static JSONArray lexicalEntries(JSONObject items) {
        try {
            return items.getJSONArray("lexicalEntries");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject lexicalEntryObject(JSONArray lexicalEntries, int i) {
        try {
            return lexicalEntries.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    // there could be a few objects (which contain lexical category: noun, verb, etc.)
    private static JSONObject lexicalCategoryObject(JSONObject lexicalEntryObject) {
        try {
            return lexicalEntryObject.getJSONObject("lexicalCategory");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static String lexicalCategoryString(JSONObject lexicalCategoryObject) {
        try {
            return lexicalCategoryObject.getString("text");
        } catch (JSONException e) {
            return "";
        }
    }

    private static JSONArray entries(JSONObject lexicalEntryObject) {
        try {
            return lexicalEntryObject.getJSONArray("entries");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject entryObject(JSONArray entries, int i) {
        try {
            return entries.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static JSONArray pronunciations(JSONObject entryObject) {
        try {
            return entryObject.getJSONArray("pronunciations");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject pronunciationObject(JSONArray pronunciations, int i) {
        try {
            return pronunciations.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static String phoneticSpelling(JSONObject pronunciationObject) {
        try {
            return pronunciationObject.getString("phoneticSpelling");
        } catch (JSONException e) {
            return "";
        }
    }

    // pronunciations' object might not contain audioLink. So you have to get next object contains it!
    private static String audioFile(JSONObject pronunciationsObject) {
        try {
            return pronunciationsObject.getString("audioFile");
        } catch (JSONException e) {
            return "";
        }
    }

    private static JSONArray senses(JSONObject entryObject) {
        try {
            return entryObject.getJSONArray("senses");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject sensesObject(JSONArray senses, int i) {
        try {
            return senses.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }




    private static JSONArray subsenses(JSONObject sensesObject) {
        try {
            return sensesObject.getJSONArray("subsenses");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject subsensesObject(JSONArray subsenses, int i) {
        try {
            return subsenses.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static JSONArray subsensesSnonyms(JSONObject subsensesObject) {
        try {
            return subsensesObject.getJSONArray("synonyms");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject subsensesSynonymsObject(JSONArray susensesSnonyms, int i) {
        try {
            return susensesSnonyms.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static String subsensesSynonymString(JSONObject subsensesSynonymsObject) {
        try {
            return subsensesSynonymsObject.getString("text");
        } catch (JSONException e) {
            return "";
        }
    }

    private static StringBuilder getSubsensesSynonyms(JSONObject sensesObject) {
        StringBuilder synonyms = new StringBuilder("");
        JSONArray subsenses = subsenses(sensesObject);
        if (subsenses.length() > 0) {
            for (int l = 0; l < subsenses.length(); l++) {
                JSONObject subsensesObject = subsensesSynonymsObject(subsenses, l);

                JSONArray subsensesSynonyms = subsensesSnonyms(subsensesObject);
                for (int m = 0; m < subsensesSynonyms.length(); m++) {
                    JSONObject subsensesSynonymsObect = subsensesSynonymsObject(subsensesSynonyms, m);
                    if (!synonyms.toString().isEmpty()) {
                        synonyms.append("\n");
                    }
                    synonyms.append(subsensesSynonymString(subsensesSynonymsObect));
                }
            }
        }
        return synonyms;
    }



    private static JSONArray examples(JSONObject sensesObject) {
        try {
            return sensesObject.getJSONArray("examples");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject examplesObject(JSONArray examples, int i) {
        try {
            return examples.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static String exampleString(JSONObject examplesObject) {
        try {
            return examplesObject.getString("text");
        } catch (JSONException e) {
            return "";
        }
    }

    private static JSONArray getSynonyms(JSONObject sensesObject) {
        try {
            return sensesObject.getJSONArray("synonyms");
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject synonymsObject(JSONArray synonyms, int i) {
        try {
            return synonyms.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static String synonymString(JSONObject synonymsObject) {
        try {
            return synonymsObject.getString("text");
        } catch (JSONException e) {
            return "";
        }
    }
}
