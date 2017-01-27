package gg.xvoicechanger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private TextView tempoText;
    private TextView pitchText;
    private TextView rateText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int tempo;
    private int pitch;
    private int rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File soundTouchLib = new File(this.getFilesDir(), "libsoundtouch.so");

        if (!soundTouchLib.exists()){

            AssetManager assetManager = this.getAssets();

            try {
                soundTouchLib.createNewFile();
                soundTouchLib.setReadable(true, false);
                InputStream inputStream = assetManager.open("libsoundtouch.so");
                FileOutputStream fileOutputStream = new FileOutputStream(soundTouchLib);

                byte[] buffer = new byte[1024];
                    int read;
                    while((read = inputStream.read(buffer)) != -1){
                        fileOutputStream.write(buffer, 0, read);
                    }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        SeekBar tempoValue = (SeekBar) findViewById(R.id.tempoValue);
        SeekBar pitchValue = (SeekBar) findViewById(R.id.pitchValue);
        SeekBar rateValue = (SeekBar) findViewById(R.id.rateValue);
        final CheckBox enableWechatValue = (CheckBox) findViewById(R.id.enableWechat);
        final CheckBox enableTelegramValue = (CheckBox) findViewById(R.id.enableTelegram);

        Button save = (Button) findViewById(R.id.button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putInt("tempo", tempo);
                editor.putInt("pitch", pitch);
                editor.putInt("rate", rate);

                editor.putBoolean("enableWechat", enableWechatValue.isChecked());
                editor.putBoolean("enableTelegram", enableTelegramValue.isChecked());
                editor.commit();

                Toast toast = Toast.makeText(MainActivity.this, "successfully saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        MySeekBarChangeListener mySeekBarChangeListener = new MySeekBarChangeListener();

        tempoValue.setOnSeekBarChangeListener(mySeekBarChangeListener);
        pitchValue.setOnSeekBarChangeListener(mySeekBarChangeListener);
        rateValue.setOnSeekBarChangeListener(mySeekBarChangeListener);

        tempoText = (TextView) findViewById(R.id.tempoText);
        pitchText = (TextView) findViewById(R.id.pitchText);
        rateText = (TextView) findViewById(R.id.rateText);

        sharedPreferences = getSharedPreferences("XVoiceChanger", MODE_WORLD_READABLE);
        editor = sharedPreferences.edit();

        tempo = sharedPreferences.getInt("tempo", -20);
        pitch = sharedPreferences.getInt("pitch", 8);
        rate = sharedPreferences.getInt("rate", 1);
        boolean enableWechat = sharedPreferences.getBoolean("enableWechat", true);
        boolean enableTelegram = sharedPreferences.getBoolean("enableTelegram", true);

        editor.putInt("tempo", tempo);
        editor.putInt("pitch", pitch);
        editor.putInt("rate", rate);
        editor.putBoolean("enableWechat", enableWechat);
        editor.putBoolean("enableTelegram", enableTelegram);
        editor.commit();

        tempoValue.setProgress(tempo + 50);
        pitchValue.setProgress(pitch + 12);
        rateValue.setProgress(rate + 50);

        }

    class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            switch (seekBar.getId()){

                case R.id.tempoValue:
                    i -= 50;
                    tempo = i;
                    tempoText.setText(i+"");
                    break;

                case R.id.pitchValue:
                    i -= 12;
                    pitch = i;
                    pitchText.setText(i+"");
                    break;

                case R.id.rateValue:
                    i -= 50;
                    rate = i;
                    rateText.setText(i+"");
                    break;


            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    }
