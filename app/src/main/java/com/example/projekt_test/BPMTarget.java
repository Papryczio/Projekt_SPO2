package com.example.projekt_test;

public class BPMTarget {

    public BPMTarget(){

    }

    private static final String TAG = "BPMTarget";

    public String BPM_check(int BPM, int age) {
        String range = "UNKNOWN";
        // Over Limit
        // Max Effort
        // Vigorous
        // Aerobic
        // Moderate
        // Low Intensity
        // Rest
        // UNKNOWN

        if (age > 80) {
            if(BPM > 153){
                range = "Over Limit";
            }
            else if(BPM > 130){
                range = "Max Effort";
            }
            else if(BPM > 115){
                range = "Vigorous";
            }
            else if(BPM > 107){
                range = "Aerobic";
            }
            else if(BPM > 92){
                range = "Moderate";
            }
            else if(BPM > 77){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 75) {
            if(BPM > 157){
                range = "Over Limit";
            }
            else if(BPM > 133){
                range = "Max Effort";
            }
            else if(BPM > 117){
                range = "Vigorous";
            }
            else if(BPM > 110){
                range = "Aerobic";
            }
            else if(BPM > 94){
                range = "Moderate";
            }
            else if(BPM > 78){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 70) {
            if(BPM > 160){
                range = "Over Limit";
            }
            else if(BPM > 136){
                range = "Max Effort";
            }
            else if(BPM > 120){
                range = "Vigorous";
            }
            else if(BPM > 112){
                range = "Aerobic";
            }
            else if(BPM > 96){
                range = "Moderate";
            }
            else if(BPM > 80){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 65) {
            if(BPM > 163){
                range = "Over Limit";
            }
            else if(BPM > 139){
                range = "Max Effort";
            }
            else if(BPM > 123){
                range = "Vigorous";
            }
            else if(BPM > 114){
                range = "Aerobic";
            }
            else if(BPM > 98){
                range = "Moderate";
            }
            else if(BPM > 82){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 60) {
            if(BPM > 167){
                range = "Over Limit";
            }
            else if(BPM > 142){
                range = "Max Effort";
            }
            else if(BPM > 125){
                range = "Vigorous";
            }
            else if(BPM > 117){
                range = "Aerobic";
            }
            else if(BPM > 100){
                range = "Moderate";
            }
            else if(BPM > 83){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 55) {
            if(BPM > 170){
                range = "Over Limit";
            }
            else if(BPM > 145){
                range = "Max Effort";
            }
            else if(BPM > 128){
                range = "Vigorous";
            }
            else if(BPM > 119){
                range = "Aerobic";
            }
            else if(BPM > 102){
                range = "Moderate";
            }
            else if(BPM > 85){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }

        } else if (age > 50) {
            if(BPM > 173){
                range = "Over Limit";
            }
            else if(BPM > 147){
                range = "Max Effort";
            }
            else if(BPM > 130){
                range = "Vigorous";
            }
            else if(BPM > 121){
                range = "Aerobic";
            }
            else if(BPM > 104){
                range = "Moderate";
            }
            else if(BPM > 87){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 45) {
            if(BPM > 177){
                range = "Over Limit";
            }
            else if(BPM > 150){
                range = "Max Effort";
            }
            else if(BPM > 133){
                range = "Vigorous";
            }
            else if(BPM > 124){
                range = "Aerobic";
            }
            else if(BPM > 106){
                range = "Moderate";
            }
            else if(BPM > 88){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 40) {
            if(BPM > 180){
                range = "Over Limit";
            }
            else if(BPM > 153){
                range = "Max Effort";
            }
            else if(BPM > 135){
                range = "Vigorous";
            }
            else if(BPM > 126){
                range = "Aerobic";
            }
            else if(BPM > 108){
                range = "Moderate";
            }
            else if(BPM > 90){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 35) {
            if(BPM > 183){
                range = "Over Limit";
            }
            else if(BPM > 156){
                range = "Max Effort";
            }
            else if(BPM > 138){
                range = "Vigorous";
            }
            else if(BPM > 128){
                range = "Aerobic";
            }
            else if(BPM > 110){
                range = "Moderate";
            }
            else if(BPM > 92){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 30) {
            if(BPM > 187){
                range = "Over Limit";
            }
            else if(BPM > 159){
                range = "Max Effort";
            }
            else if(BPM > 140){
                range = "Vigorous";
            }
            else if(BPM > 131){
                range = "Aerobic";
            }
            else if(BPM > 112){
                range = "Moderate";
            }
            else if(BPM > 93){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age > 25) {
            if(BPM > 190){
                range = "Over Limit";
            }
            else if(BPM > 162){
                range = "Max Effort";
            }
            else if(BPM > 143){
                range = "Vigorous";
            }
            else if(BPM > 133){
                range = "Aerobic";
            }
            else if(BPM > 114){
                range = "Moderate";
            }
            else if(BPM > 95){
                range = "Low Intensity";
            }
            else{
                range = "Rest";
            }
        } else if (age >= 18) {
            if (BPM > 194) {
                range = "Over Limit";
            } else if (BPM > 164) {
                range = "Max Effort";
            } else if (BPM > 145) {
                range = "Vigorous";
            } else if (BPM > 135) {
                range = "Aerobic";
            } else if (BPM > 116) {
                range = "Moderate";
            } else if (BPM > 97) {
                range = "Low Intensity";
            } else{
                range = "Rest";
            }
        }
        else {
            range = "UNKNOWN";
        }
        return range;
    }
}




