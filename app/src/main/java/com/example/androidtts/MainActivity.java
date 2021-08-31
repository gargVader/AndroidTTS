package com.example.androidtts;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    String TEXT_VN = "Chào mừng bạn đến với Trợ lý bán lẻ tiếng lóng. Bạn đang tìm kiếm sản phẩm nào?";
    int e = 1, h = 1, k = 1, v = 0;
    int e_end = 5, h_end = 4, k_end = 2; // starting index for male voice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setSpeechRate(1.0f);
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
                        v.getLocale().toString().equals("kn_IN") ||
                        v.getLocale().toString().equals("vi_VN"))
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
            if ((o1.getName().charAt(0)) - (o2.getName().charAt(0)) == 0) {
                return o1.getName().compareTo(o2.getName());
            }
            return (o1.getName().charAt(0)) - (o2.getName().charAt(0));
        }
    }

    void setupButtons(ArrayList<Voice> voices) {

        Log.d(TAG, "setupButtons: " + voices.size());

        ContentResolver contentResolver = this.getContentResolver();
        int speechRate = Settings.Secure.getInt(contentResolver, Settings.Secure.TTS_DEFAULT_RATE, 100);
        Log.d(TAG, "speechRate: " + speechRate);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        TextView textView = new TextView(this);
        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
        textView.setText("Your system default TTS speech rate is=" + (speechRate / 100.0));
        linearLayout.addView(textView);

        Collections.sort(voices, new SortByName());

        for (int i = 0; i < voices.size(); i++) {
            Button button = new Button(this);
            Voice voice = voices.get(i);

            button.setText(getButtonName(voice.getName().charAt(0), i));
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TTS.setVoice(voice);
                    Log.d(TAG, "onClick: " + voice.getName());
                    ((TextView) findViewById(getId(voice.getName().charAt(0)))).setText(voice.getName());
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
                        case "vi_VN":
                            TTS.speak(TEXT_VN, TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        default:
                            TTS.speak(TEXT_EN, TextToSpeech.QUEUE_FLUSH, null, null);

                    }

                }
            });

            if (i == 0 || voice.getName().charAt(0) != voices.get(i - 1).getName().charAt(0)) {
                TextView headingTextView = new TextView(this);
                headingTextView.setText(getHeading(voice.getName().charAt(0)));
                headingTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Title);
                if (i != 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 25, 0, 0);
                    headingTextView.setLayoutParams(params);
                }
                linearLayout.addView(headingTextView);

                TextView nameTextView = new TextView(this);
                nameTextView.setText("Please select a voice to show its identifier");
                nameTextView.setId(getId(voice.getName().charAt(0)));
                nameTextView.setTextIsSelectable(true);
                linearLayout.addView(nameTextView);
            }

            linearLayout.addView(button);
        }

    }

    String getHeading(char c) {
        switch (c) {
            case 'e':
                return "English Voice";
            case 'h':
                return "Hindi Voice";
            case 'k':
                return "Kannada Voice";
            case 'v':
                return "Vietnamese Voice";
        }
        return "";
    }

    int getId(char c) {
        switch (c) {
            case 'e':
                return R.id.English;
            case 'h':
                return R.id.Hindi;
            case 'k':
                return R.id.Kannada;
            case 'v':
                return R.id.Vietnamese;
        }
        return R.id.English;
    }


    String getButtonName(char c, int idx) {
        String name;
        switch (c) {
            case 'e':
                name = "Voice " + (e++);
                if (e > e_end) name += " Male";
                else name += " Female";
                return name;
            case 'h':
                name = "Voice " + (h++);
                if (h > h_end) name += " Male";
                else name += " Female";
                return name;
            case 'k':
                name = "Voice " + (k++);
                if (k > k_end) name += " Male";
                else name += " Female";
                return name;
            case 'v':
                name = "Voice " + (v + 1);

                if (v <= 1 || v == 3) name += " Female";
                if (v == 2 || v == 4) name += " Male";
                v++;
                return name;
        }
        return "";
    }


}