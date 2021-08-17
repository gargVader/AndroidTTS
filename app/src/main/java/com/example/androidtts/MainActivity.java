package com.example.androidtts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextToSpeech TTS;
    public static final String TAG = "Slang";
    String TEXT_EN = "Welcome to Slang Retail Assistant. What product are you looking for?";
    String TEXT_HI = "स्लैंग रिटेल असिस्टेंट में आपका स्वागत है। आप किस उत्पाद की तलाश में हैं?";
    String TEXT_KN = "ಗ್ರಾಮ್ಯ ಚಿಲ್ಲರೆ ಸಹಾಯಕರಿಗೆ ಸುಸ್ವಾಗತ. ನೀವು ಯಾವ ಉತ್ಪನ್ನವನ್ನು ಹುಡುಕುತ್ತಿದ್ದೀರಿ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    LongTask longTask = new LongTask(MainActivity.this);
                    longTask.execute(TTS);
                }
            }
        });
    }

    private static class LongTask extends AsyncTask<Object, Void, ArrayList<Voice>> {

        MainActivity mainActivity;

        public LongTask(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected ArrayList<Voice> doInBackground(Object... objects) {
            ArrayList<Voice> voices = new ArrayList<>();
            TextToSpeech TTS = (TextToSpeech) objects[0];
            Set<Voice> allVoices = TTS.getVoices();
            for (Voice v : allVoices) {
                if ((v.getLocale().toString().equals("en_IN") ||
                        v.getLocale().toString().equals("hi_IN") ||
                        v.getLocale().toString().equals("kn_IN"))
                        && v.getName().contains("network")) {
                    Log.d(TAG, v.toString());
                    voices.add(v);
                }
            }
            return voices;
        }

        @Override
        protected void onPostExecute(ArrayList<Voice> voices) {
            super.onPostExecute(voices);
            mainActivity.setupButtons(voices);
        }
    }

    static class SortByName implements Comparator<Voice> {

        @Override
        public int compare(Voice o1, Voice o2) {
            return (o1.getName().charAt(0)) - (o2.getName().charAt(0));
        }
    }

    void setupButtons(ArrayList<Voice> voices) {

        Log.d(TAG, "setupButtons: " + voices.size());
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        Collections.sort(voices, new SortByName());

        for (Voice voice : voices) {
            Button button = new Button(this);

            button.setText(voice.getName());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TTS.setVoice(voice);
                    Log.d(TAG, "onClick: "+voice.getLocale().toString());
                    switch (voice.getLocale().toString()) {
                        case "en_IN":
                            TTS.speak(TEXT_EN, TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        case "hi_IN":
                            TTS.speak(TEXT_HI, TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        case "kn_IN":
                            TTS.speak(TEXT_KN, TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        default:
                            TTS.speak(TEXT_EN, TextToSpeech.QUEUE_FLUSH, null, null);

                    }

                }
            });

            linearLayout.addView(button);
        }

    }


}