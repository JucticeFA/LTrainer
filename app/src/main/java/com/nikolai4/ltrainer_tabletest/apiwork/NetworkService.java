package com.nikolai4.ltrainer_tabletest.apiwork;

// dict.1.1.20230827T095647Z.a0a3889d81f27a84.e4b7f10161413ef6b5070e9562c2a7a628cf9b5c - yandex key
// https://api.dictionaryapi.dev/api/v2/entries/en/ - dictionaryapi.dev
// https://wordsapiv1.p.mashape.com/words/

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class NetworkService {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    // https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=API-ключ&lang=en-ru&text=time
    private static final String BASE_YANDEX_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?"; // find out if it's the right way
    private static final String YANDEX_WORD_KEY = "text";
    private static final String YANDEX_KEY_KEY = "key";
    private static final String YANDEX_KEY_VALUE = "dict.1.1.20230827T095647Z.a0a3889d81f27a84.e4b7f10161413ef6b5070e9562c2a7a628cf9b5c";

    // dictionaryapi.dev
    private static final String BASE_DICT_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    // wordsapi
    // "https://wordsapiv1.p.mashape.com/words/soliloquy"
    // header: .setHeader("X-RapidAPI-Key", "c393663511msh96bf3cbd2f6a1c7p14297cjsn096c995123e1")
    private static final String BASE_WORDSAPI_URL = "https://wordsapiv1.p.mashape.com/words/";

    private static String mAudioLink = "";

    /*
    From yandex: translates, transcription, frequency, part of speech
    From dictionaryapi.dev: pronunciation, examples
     */

    public static NetDataContainer startDataFetchingTask(String word) {
        NetDataContainer container = null;
        Future<NetDataContainer> future = executor.submit(new Callable<NetDataContainer>() {
            @Override
            public NetDataContainer call() throws Exception {
                String yandexData = getStringJSONObject(word, BASE_YANDEX_URL);
                String dictionarydevData = getStringJSONObject(word, BASE_DICT_URL);
                return getNetDataContainer(yandexData, dictionarydevData);
            }
        });
        try {
            container = future.get();
            Log.d("startDataFetchingTask", "startDataFetchingTask: " + container.getExamples().toString());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return container;
    }

    public static String getStringJSONObject(String word, String link) {
        String jsonResult = "";
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        Uri uri = Uri.parse(BASE_WORDSAPI_URL + word).buildUpon().build();

        switch (link) {
            case BASE_YANDEX_URL:
                uri = Uri.parse(BASE_YANDEX_URL).buildUpon()
                        .appendQueryParameter(YANDEX_KEY_KEY, YANDEX_KEY_VALUE)
                        .appendQueryParameter("lang", "en-ru")
                        .appendQueryParameter(YANDEX_WORD_KEY, word)
                        .build();
                break;
            case BASE_DICT_URL:
                uri = Uri.parse(BASE_DICT_URL + word).buildUpon().build();
                break;
        }

        try {
            URL url = new URL(uri.toString());
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            jsonResult = data.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("jsonResult", "jsonResult: " + jsonResult);
        return jsonResult;
    }

    public static NetDataContainer getNetDataContainer(String yandexJSON, String dictionarydevJSON) {
        NetDataContainer container;

        List<String> translates = new ArrayList<>();
        String transcription = "";
//        int frequency = 0;
        List<String> audioLinks = new ArrayList<>();
        List<String> examples = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(yandexJSON);
            JSONArray def = mainObject.getJSONArray("def");
            for (int i = 0; i < def.length(); i++) {
                if (transcription.isEmpty()) {
                    transcription = "["+def.getJSONObject(i).getString("ts")+"]";
                }
                JSONArray tr = def.getJSONObject(i).getJSONArray("tr");
                for (int j = 0; j < tr.length(); j++) {
                    String text = tr.getJSONObject(j).getString("text");
                    translates.add(text);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray mainArray = new JSONArray(dictionarydevJSON);
            JSONObject item = mainArray.getJSONObject(0);

            JSONArray meanings = item.getJSONArray("meanings");
            JSONArray phonetics = item.getJSONArray("phonetics");

            for (int i = 0; i < phonetics.length(); i++) {
                JSONObject phoneticObject = phonetics.getJSONObject(i);
                if (phoneticObject.has("audio")) {
                    audioLinks.add(phoneticObject.getString("audio"));
                }
            }
            for (int i = 0; i < meanings.length(); i++) {
                JSONObject meaningObject = meanings.getJSONObject(i);
                JSONArray definitions = meaningObject.getJSONArray("definitions");
                for (int j = 0; j < definitions.length(); j++) {
                    JSONObject definitionObject = definitions.getJSONObject(j);
                    if (definitionObject.has("example")) {
                        examples.add(definitionObject.getString("example"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        container = new NetDataContainer(translates, transcription, audioLinks, examples);
        Log.d("getNetDataContainer", "getNetDataContainer: " + container.getExamples().toString());
        return container;
    }

    /**
     Scoops translates from Yandex
     * @param jsonString
     * @return
     */
    public static List<String> getTranslates(String jsonString) {
        List<String> translates = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray def = mainObject.getJSONArray("def");
            for (int i = 0; i < def.length(); i++) {
                JSONArray tr = def.getJSONObject(i).getJSONArray("tr");
                for (int j = 0; j < tr.length(); j++) {
                    String text = tr.getJSONObject(j).getString("text");
                    translates.add(text);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return translates;
    }

    /**
     Scoops transcription from Yandex
     * @param jsonString
     * @return
     */
    public static String getTranscription(String jsonString) {
        String transcription = "";
        try {
            JSONArray mainArray = new JSONObject(jsonString).getJSONArray("items");
            transcription = mainArray.getJSONObject(0).getString("phonetic");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return transcription;
    }

//    public static String getFrequency(String jsonString) {
//        int frequency = "";
//        try {
//            JSONArray mainArray = new JSONObject(jsonString).getJSONArray("items");
//            frequency = mainArray.getJSONObject(0).getString("phonetic");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return frequency;
//    }



//    public static List<String> partsOfSpeech(String jsonString) {
//        List<String> parts = new ArrayList<>();
//        try {
//            JSONArray mainArray = new JSONObject(jsonString).getJSONArray("items");
//            parts = mainArray.getJSONObject(0).getString("phonetic");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return parts;
//    }

//    public static List<String> getSynonyms(String jsonString) {
//
//    }

    /**
     Scoops examples from Dictionaryapi.dev
     * @param jsonString
     * @return examples
     */
    public static List<String> getExamples(String jsonString) {
        List<String> examples = new ArrayList<>();
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray mainArray = mainObject.getJSONArray("items");

            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject item = mainArray.getJSONObject(i);
                JSONArray meanings = item.getJSONArray("meanings");
                for (int j = 0; j < meanings.length(); j++) {
                    JSONObject meaningObject = meanings.getJSONObject(j);
                    JSONArray definitions = meaningObject.getJSONArray("definitions");
                    for (int k = 0; k < definitions.length(); k++) {
                        JSONObject definitionObject = definitions.getJSONObject(i);
                        if (definitionObject.has("example")) {
                            examples.add(definitionObject.getString("example"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return examples;
    }

    /**
     Scoops audio links from Dictionaryapi.dev
     * @param jsonString
     * @return audio links
     */
    public static List<String> getAudioLink(String jsonString) {
        List<String> links = new ArrayList<>();
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray mainArray = mainObject.getJSONArray("items");

            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject item = mainArray.getJSONObject(i);
                JSONArray phonetics = item.getJSONArray("phonetics");
                for (int j = 0; j < phonetics.length(); j++) {
                    JSONObject phoneticObject = phonetics.getJSONObject(j);
                    if (phoneticObject.has("audio")) {
                        links.add(phoneticObject.getString("audio"));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return links;
    }

//    public static String startExecutionTask(String word) {
//
//    }

//    public static String getStringJSONObject(String word, String link) {
////        AsyncHttpClient client = new DefaultAsyncHttpClient();
////        client.prepare("POST", "https://yandextranslatezakutynskyv1.p.rapidapi.com/detectLanguage")
////                .setHeader("content-type", "application/x-www-form-urlencoded")
////                .setHeader("X-RapidAPI-Key", "c393663511msh96bf3cbd2f6a1c7p14297cjsn096c995123e1")
////                .setHeader("X-RapidAPI-Host", "YandexTranslatezakutynskyV1.p.rapidapi.com")
////                .setBody("text=%3CREQUIRED%3E")
////                .execute()
////                .toCompletableFuture()
////                .thenAccept(System.out::println)
////                .join();
////
////        client.close();
//
//        String jsonResult = "";
//        HttpsURLConnection connection = null;
//        BufferedReader reader = null;
//        Uri uri = Uri.parse(BASE_WORDSAPI_URL + word).buildUpon().build();
//
//        switch (link) {
//            case BASE_YANDEX_URL:
//                uri = Uri.parse(BASE_YANDEX_URL).buildUpon()
//                        .appendQueryParameter(YANDEX_KEY_KEY, YANDEX_KEY_VALUE)
//                        .appendQueryParameter("lang", "en-ru")
//                        .appendQueryParameter(YANDEX_WORD_KEY, word)
//                        .build();
//                break;
//            case BASE_DICT_URL:
//                uri = Uri.parse(BASE_DICT_URL + word).buildUpon().build();
//                break;
//            case BASE_WORDSAPI_URL:
//                uri = Uri.parse(BASE_WORDSAPI_URL + word).buildUpon().build();
//                break;
//        }
//
//        try {
//            URL url = new URL(uri.toString());
//            connection = (HttpsURLConnection) url.openConnection();
//            connection.setRequestProperty("X-RapidAPI-Key", "c393663511msh96bf3cbd2f6a1c7p14297cjsn096c995123e1");
//            connection.setRequestMethod("GET");
//            connection.connect();
//
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            StringBuilder data = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                data.append(line);
//            }
//            jsonResult = data.toString();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Log.d("jsonResult", "jsonResult: " + jsonResult);
//        return jsonResult;
//    }

//    public static List<String> getTranslates(String jsonString) {
//        List<String> translates = new ArrayList<>();
//
//        try {
//            JSONObject mainObject = new JSONObject(jsonString);
//            JSONArray def = mainObject.getJSONArray("def");
//            for (int i = 0; i < def.length(); i++) {
//                JSONArray tr = def.getJSONObject(i).getJSONArray("tr");
//                for (int j = 0; j < tr.length(); j++) {
//                    String text = tr.getJSONObject(j).getString("text");
//                    translates.add(text);
//                }
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return translates;
//    }
//
//    // i need to get: transcription, synonyms, examples, pronunciation (link to mp3), frequency
//
//    public static String getTranscription(String jsonString) {
//        String transcription = "";
//        try {
//            JSONArray mainArray = new JSONObject(jsonString).getJSONArray("items");
//            transcription = mainArray.getJSONObject(0).getString("phonetic");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return transcription;
//    }
//
////    public static List<String> lexicalCategories(String jsonString) {
////
////    }
//
////    public static List<String> getSynonyms(String jsonString) {
////
////    }
//
//    public static List<String> getExamples(String jsonString) {
//        List<String> examples = new ArrayList<>();
//        try {
//            JSONObject mainObject = new JSONObject(jsonString);
//            JSONArray mainArray = mainObject.getJSONArray("items");
//
//            for (int i = 0; i < mainArray.length(); i++) {
//                JSONObject item = mainArray.getJSONObject(i);
//                JSONArray meanings = item.getJSONArray("meanings");
//                for (int j = 0; j < meanings.length(); j++) {
//                    JSONObject meaningObject = meanings.getJSONObject(j);
//                    JSONArray definitions = meaningObject.getJSONArray("definitions");
//                    for (int k = 0; k < definitions.length(); k++) {
//                        JSONObject definitionObject = definitions.getJSONObject(i);
//                        if (definitionObject.has("example")) {
//                            examples.add(definitionObject.getString("example"));
//                        }
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return examples;
//    }
//
//    public static List<String> getAudioLink(String jsonString) {
//        List<String> links = new ArrayList<>();
//        try {
//            JSONObject mainObject = new JSONObject(jsonString);
//            JSONArray mainArray = mainObject.getJSONArray("items");
//
//            for (int i = 0; i < mainArray.length(); i++) {
//                JSONObject item = mainArray.getJSONObject(i);
//                JSONArray phonetics = item.getJSONArray("phonetics");
//                for (int j = 0; j < phonetics.length(); j++) {
//                    JSONObject phoneticObject = phonetics.getJSONObject(j);
//                    if (phoneticObject.has("audio")) {
//                        links.add(phoneticObject.getString("audio"));
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return links;
//    }
}

