package com.example.englishelearning.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishelearning.R;
import com.example.englishelearning.RasaApi;
import com.example.englishelearning.adapter.ConversationAdapter;
import com.example.englishelearning.api.RetrofitClient;
import com.example.englishelearning.model.Message;
import com.example.englishelearning.model.RasaRequest;
import com.example.englishelearning.model.RasaResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedFragment extends Fragment {
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private ConversationAdapter conversationAdapter;
    private RasaApi rasaApi;
    private boolean isRecording = false;
    private EditText etMessage;
    private HashMap<String, String> spellCheckMap;
    private String senderId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savedai, container, false);

        // Chỉ tạo senderId mới nếu chưa có
        if (senderId == null) {
            senderId = "user_" + System.currentTimeMillis();
        }

        // Khởi tạo danh sách gợi ý chính tả
        spellCheckMap = new HashMap<>();
        spellCheckMap.put("hii", "Hi");
        spellCheckMap.put("helo", "Hello");
        spellCheckMap.put("helllo", "Hello");
        spellCheckMap.put("hlo", "Hello");
        spellCheckMap.put("heyy", "Hey");
        spellCheckMap.put("byee", "Bye");
        spellCheckMap.put("wat", "What");
        spellCheckMap.put("wats", "What's");
        spellCheckMap.put("piza", "Pizza");
        spellCheckMap.put("coffe", "Coffee");
        spellCheckMap.put("sandwitch", "Sandwich");
        spellCheckMap.put("tiket", "Ticket");
        spellCheckMap.put("flite", "Flight");
        spellCheckMap.put("wether", "Weather");
        spellCheckMap.put("hows", "How's");
        spellCheckMap.put("wheres", "Where's");
        spellCheckMap.put("sup", "What's up");
        spellCheckMap.put("tomorow", "Tomorrow");
        spellCheckMap.put("travling", "Traveling");
        spellCheckMap.put("musick", "Music");
        spellCheckMap.put("confrim", "Confirm");
        spellCheckMap.put("larg", "Large");
        spellCheckMap.put("chese", "Cheese");
        spellCheckMap.put("umbrelia", "Umbrella");
        spellCheckMap.put("directons", "Directions");
        spellCheckMap.put("drnk", "Drink");
        spellCheckMap.put("delivry", "Delivery");
        spellCheckMap.put("hurry", "Hurry");
        spellCheckMap.put("temp", "Temperature");
        spellCheckMap.put("humidty", "Humidity");
        spellCheckMap.put("sunrise", "Sunrise");
        spellCheckMap.put("sunset", "Sunset");
        spellCheckMap.put("forcast", "Forecast");
        spellCheckMap.put("econmy", "Economy");
        spellCheckMap.put("clas", "Class");
        spellCheckMap.put("paymnt", "Payment");
        spellCheckMap.put("movis", "Movies");
        spellCheckMap.put("thnx", "Thanks");
        spellCheckMap.put("dessert", "Dessert");
        spellCheckMap.put("promo", "Promotion");
        spellCheckMap.put("luggages", "Luggage");
        spellCheckMap.put("stopovers", "Stopover");
        spellCheckMap.put("hobby", "Hobby");

        // Khởi tạo RecyclerView
        conversationAdapter = new ConversationAdapter();
        RecyclerView rvConversation = view.findViewById(R.id.rv_conversation);
        rvConversation.setAdapter(conversationAdapter);
        rvConversation.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo EditText
        etMessage = view.findViewById(R.id.et_message);

        // Khởi tạo TextToSpeech
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    Log.d("TTS", "TextToSpeech initialized successfully");
                } else {
                    Log.e("TTS", "TextToSpeech initialization failed: " + status);
                    conversationAdapter.addMessage(new Message("TextToSpeech initialization failed.", false));
                }
            }
        });

        // Khởi tạo SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                isRecording = false;
                MaterialButton btnRecord = view.findViewById(R.id.btn_record);
                btnRecord.setText("Record");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String userInput = matches.get(0);
                    Log.d("SpeechRecognizer", "Recognized: " + userInput);
                    conversationAdapter.addMessage(new Message(userInput, true));
                    callRasaApi(userInput);
                } else {
                    Log.e("SpeechRecognizer", "No results found");
                    conversationAdapter.addMessage(new Message("No speech results found. Try speaking clearly or typing.", false));
                }
            }

            @Override
            public void onError(int error) {
                isRecording = false;
                MaterialButton btnRecord = view.findViewById(R.id.btn_record);
                btnRecord.setText("Record");
                String errorMessage;
                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "No speech match found. Try speaking clearly, using a longer sentence, or typing your message.";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "Client error. Please wait a moment and try again.";
                        break;
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "Audio recording error. Check your microphone.";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "Microphone permission denied. Please grant permission.";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "Network error. Check your connection and try again.";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "Speech timeout. Try speaking longer or louder.";
                        break;
                    default:
                        errorMessage = "Speech error: " + error;
                }
                Log.e("SpeechRecognizer", errorMessage);
                conversationAdapter.addMessage(new Message(errorMessage, false));
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("SpeechRecognizer", "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Speech started");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Speech ended");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
            @Override
            public void onPartialResults(Bundle partialResults) {}
        });

        // Nút gửi tin nhắn
        MaterialButton btnSend = view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = etMessage.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    conversationAdapter.addMessage(new Message(userInput, true));
                    callRasaApi(userInput);
                    etMessage.setText("");
                }
            }
        });

        // Nút ghi âm
        MaterialButton btnRecord = view.findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    conversationAdapter.addMessage(new Message("Microphone permission required.", false));
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, 100);
                    return;
                }
                if (isRecording) {
                    speechRecognizer.stopListening();
                    isRecording = false;
                    btnRecord.setText("Record");
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
                    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);
                    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);
                    speechRecognizer.startListening(intent);
                    isRecording = true;
                    btnRecord.setText("Stop");
                }
            }
        });

        // Yêu cầu quyền RECORD_AUDIO
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{android.Manifest.permission.RECORD_AUDIO},
                100
        );

        // Khởi tạo Rasa API
        rasaApi = RetrofitClient.getRasaApi();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permissions", "Microphone permission granted");
        } else {
            Log.e("Permissions", "Microphone permission denied");
            conversationAdapter.addMessage(new Message("Please grant microphone permission.", false));
        }
    }

    private void callRasaApi(String userInput) {
        // Tách câu thành các từ
        String[] words = userInput.toLowerCase().split("\\s+");
        StringBuilder correctedInput = new StringBuilder();
        boolean hasCorrection = false;

        // Kiểm tra từng từ với spellCheckMap
        for (String word : words) {
            String suggestion = spellCheckMap.get(word);
            if (suggestion != null) {
                String suggestionMessage = "Do you mean '" + suggestion + "'?";
                conversationAdapter.addMessage(new Message(suggestionMessage, false));
                textToSpeech.speak(suggestionMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                correctedInput.append(suggestion).append(" ");
                hasCorrection = true;
            } else {
                correctedInput.append(word).append(" ");
            }
        }

        // Loại bỏ khoảng trắng thừa và gửi câu đã sửa đến Rasa
        String finalInput = correctedInput.toString().trim();

        RasaRequest request = new RasaRequest(senderId, finalInput);
        boolean finalHasCorrection = hasCorrection;
        rasaApi.sendMessage(request).enqueue(new Callback<List<RasaResponse>>() {
            @Override
            public void onResponse(Call<List<RasaResponse>> call, Response<List<RasaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String botReply = response.body().get(0).getText();
                    Log.d("RasaAPI", "Reply: " + botReply);
                    textToSpeech.speak(botReply, TextToSpeech.QUEUE_FLUSH, null, null);
                    conversationAdapter.addMessage(new Message(botReply, false));
                } else {
                    Log.e("RasaAPI", "No response or empty response");
                    String errorMessage = "Sorry, I didn't understand. Try rephrasing or check your spelling.";
                    if (!finalHasCorrection) {
                        String closestMatch = findClosestMatch(userInput.toLowerCase());
                        if (closestMatch != null) {
                            errorMessage = "Sorry, I didn't understand. Do you mean '" + closestMatch + "'?";
                        }
                    }
                    textToSpeech.speak(errorMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                    conversationAdapter.addMessage(new Message(errorMessage, false));
                }
            }

            @Override
            public void onFailure(Call<List<RasaResponse>> call, Throwable t) {
                Log.e("RasaAPI", "Error: " + t.getMessage());
                String errorMessage = "Error occurred. Please check your network and try again.";
                textToSpeech.speak(errorMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                conversationAdapter.addMessage(new Message(errorMessage, false));
            }
        });
    }

    private String findClosestMatch(String input) {
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = null;
        for (String key : spellCheckMap.keySet()) {
            int distance = levenshteinDistance(input, key);
            if (distance < minDistance && distance <= 3) {
                minDistance = distance;
                closestMatch = spellCheckMap.get(key);
            }
        }
        return closestMatch;
    }

    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[len1][len2];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}