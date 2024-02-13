package com.nikolai4.ltrainer_tabletest.apiwork;

import java.util.List;

//    From yandex: translates, transcription, frequency, part of speech
//    From dictionaryapi.dev: pronunciation, examples
public class NetDataContainer {
    private List<String> translates;
    private String transcription;
//    private List<Integer> frequency;
    private List<String> audioLinks;
    private List<String> examples;
    private static int nextLink = 0;

    public NetDataContainer(List<String> translates, String transcription,
                            List<String> audioLinks, List<String> examples) {
        this.translates = translates;
        this.transcription = transcription;
//        this.frequency = frequency;
        this.audioLinks = audioLinks;
        this.examples = examples;
    }

    public String getNextAudioLink() {
        if (audioLinks.isEmpty()) {
            return "";
        }
        nextLink++;
        if (nextLink > audioLinks.size()-1) {
            nextLink = 0;
        }
        return audioLinks.get(nextLink);
    }

    public List<String> getTranslates() {
        return translates;
    }

    public void setTranslates(List<String> translates) {
        this.translates = translates;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

//    public List<Integer> getFrequency() {
//        return frequency;
//    }
//
//    public void setFrequency(List<Integer> frequency) {
//        this.frequency = frequency;
//    }

    public List<String> getAudioLinks() {
        return audioLinks;
    }

    public void setAudioLinks(List<String> audioLinks) {
        this.audioLinks = audioLinks;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }
}
