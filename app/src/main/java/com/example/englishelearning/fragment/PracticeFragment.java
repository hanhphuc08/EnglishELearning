package com.example.englishelearning.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.englishelearning.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class PracticeFragment extends Fragment {
    private EditText inputText;
    private Button translateButton;
    private ImageButton clearButton;
    private ImageButton speakButton;
    private TextView resultTranslation;
    private Button copyButton;
    private TextToSpeech tts;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        inputText = view.findViewById(R.id.input_text);
        translateButton = view.findViewById(R.id.translate_button);
        clearButton = view.findViewById(R.id.clear_button);
        speakButton = view.findViewById(R.id.speak_button);
        resultTranslation = view.findViewById(R.id.result_translation);
        copyButton = view.findViewById(R.id.copy_button);

        if (inputText == null || clearButton == null || translateButton == null ||
                speakButton == null || resultTranslation == null || copyButton == null) {
            Toast.makeText(requireContext(), "One or more views not found in layout", Toast.LENGTH_LONG).show();
            return view;
        }


        sharedPreferences = requireContext().getSharedPreferences("TranslationHistory", Context.MODE_PRIVATE);


        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });


        translateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập từ hoặc đoạn văn", Toast.LENGTH_SHORT).show();
                return;
            }
            translateText(text);
        });

        clearButton.setOnClickListener(v -> {
            inputText.setText("");
            resultTranslation.setText("Kết quả dịch sẽ hiển thị ở đây");
            copyButton.setVisibility(View.GONE);
        });


        speakButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập văn bản để phát âm", Toast.LENGTH_SHORT).show();
            }
        });


        copyButton.setOnClickListener(v -> {
            String text = resultTranslation.getText().toString();
            if (!text.equals("Kết quả")) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
                Toast.makeText(requireContext(), "Đã sao chép kết quả", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void translateText(String text) {
        try {

            String encodedText = URLEncoder.encode(text, "UTF-8");
            String url = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=en|vi";

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String translatedText = response
                                .getJSONObject("responseData")
                                .getString("translatedText");
                        resultTranslation.setText(translatedText);
                        copyButton.setVisibility(View.VISIBLE);
                        saveToHistory(text, translatedText);
                    } catch (Exception e) {
                        resultTranslation.setText("Lỗi khi dịch: " + e.getMessage());
                        copyButton.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Lỗi phân tích kết quả", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    resultTranslation.setText("Lỗi kết nối: " + throwable.getMessage());
                    copyButton.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Không thể kết nối đến API", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            resultTranslation.setText("Lỗi: " + e.getMessage());
            copyButton.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Lỗi xử lý văn bản", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToHistory(String original, String translated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> history = sharedPreferences.getStringSet("history", new HashSet<>());
        history.add(original + " → " + translated);
        editor.putStringSet("history", history);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}