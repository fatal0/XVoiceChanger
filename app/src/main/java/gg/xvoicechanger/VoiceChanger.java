package gg.xvoicechanger;

import android.util.Log;

import net.surina.soundtouch.SoundTouch;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class VoiceChanger implements IXposedHookLoadPackage {

    private XSharedPreferences xSharedPreferences;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if(!lpparam.packageName.equals("org.telegram.messenger") && !lpparam.packageName.equals("org.telegram.plus") && !lpparam.packageName.equals("com.tencent.mm")){
            return;
        }

        xSharedPreferences = new XSharedPreferences("gg.xvoicechanger", "XVoiceChanger");

        final Class<?> audioRecordClazz = findClass("android.media.AudioRecord", lpparam.classLoader);

        XposedBridge.hookAllConstructors(audioRecordClazz, new XC_MethodHook(){

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.d("XVoiceChanger", "Call Constructors");
                Log.d("XVoiceChanger", "args size = "+param.args.length);
                int i = 0 ;
                for (Object obj : param.args){
                    Log.d("XVoiceChanger", "args["+i+"] = "+obj.toString());
                    i++;
                }


            }



        });

        XposedBridge.hookAllMethods(audioRecordClazz, "read", new XC_MethodHook(){

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                Log.d("XVoiceChanger", "Call read");

                xSharedPreferences.reload();

                if (lpparam.packageName.equals("com.tencent.mm") && xSharedPreferences.getBoolean("enableWeChat", false)){
                    Log.d("XVoiceChanger", lpparam.packageName);
                    Log.d("XVoiceChanger", "args[0] = "+((byte[])param.args[0]).length);
                    Log.d("XVoiceChanger", "args[2] before = "+ param.args[2]);
                    param.args[2] = (int) param.args[2] - 1024;
                    Log.d("XVoiceChanger", "args[2] after = "+ param.args[2]);

                }
                else if(lpparam.packageName.equals("org.telegram.messenger") && xSharedPreferences.getBoolean("enableTelegram", false) || lpparam.packageName.equals("org.telegram.plus") && xSharedPreferences.getBoolean("enablePlusMessenger", false)){
                    Log.d("XVoiceChanger", lpparam.packageName);
                    Log.d("XVoiceChanger", "args[0] = "+((ByteBuffer)param.args[0]).capacity());
                    Log.d("XVoiceChanger", "args[1] before = "+ param.args[1]);
                    param.args[1] = (int) param.args[1] - 1024;
                    Log.d("XVoiceChanger", "args[1] after = "+ param.args[1]);
                }

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                xSharedPreferences.reload();

                if (lpparam.packageName.equals("com.tencent.mm") && xSharedPreferences.getBoolean("enableWeChat", false)){

                    Log.d("XVoiceChanger", lpparam.packageName);

                    int readsize = (int) param.getResult();

                    if (readsize >= 0) {

                        Log.d("XVoiceChanger", "before process " + readsize);

                        byte[] audioData = process((byte[]) param.args[0], readsize);

                        System.arraycopy(audioData, 0, param.args[0], 0, audioData.length);

                        Log.d("XVoiceChanger", "after process " + audioData.length);

                        param.setResult(audioData.length);

                    }

                }
                else if(lpparam.packageName.equals("org.telegram.messenger") && xSharedPreferences.getBoolean("enableTelegram", false) || lpparam.packageName.equals("org.telegram.plus") && xSharedPreferences.getBoolean("enablePlusMessenger", false)){

                    Log.d("XVoiceChanger", lpparam.packageName);

                    int readsize = (int) param.getResult();

                    if (readsize >= 0){

                        Log.d("XVoiceChanger", "before process "+readsize);

                        ByteBuffer byteBuffer = (ByteBuffer) param.args[0];

                        int position = byteBuffer.position();

                        byte[] buf = new byte[readsize];

                        byteBuffer.limit(readsize);

                        byteBuffer.get(buf, 0, readsize);

                        byteBuffer.position(position);

                        byte[] audioData = process(buf, readsize);

                        byteBuffer.limit(audioData.length);

                        byteBuffer.put(audioData, 0, audioData.length);

                        byteBuffer.position(position);

                        Log.d("XVoiceChanger", "after process "+audioData.length);

                        param.setResult(audioData.length);
                    }


                }

            }


        });

    }

    private byte[] process(byte[] data, int readsize) throws IOException {

        SoundTouch soundTouch = new SoundTouch();
        soundTouch.setSampleRate(16000);
        soundTouch.setChannels(1);

        xSharedPreferences.reload();
        soundTouch.setTempoChange((float) xSharedPreferences.getInt("tempo", -20));
        soundTouch.setPitchSemiTones((float) xSharedPreferences.getInt("pitch", 8));
        soundTouch.setRateChange((float) xSharedPreferences.getInt("rate", 1));

        short[] shortData = Utils.byteToShort(data, readsize);
        soundTouch.putSamples(shortData, shortData.length);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer;

        while (true){

            buffer = Utils.shortToByte(soundTouch.receiveSamples());

            if(buffer.length != 0){
                byteArrayOutputStream.write(buffer);
            }

            else {
                break;
            }
        }

        soundTouch.flush();

        while (true){

            buffer = Utils.shortToByte(soundTouch.receiveSamples());

            if(buffer.length != 0){
                byteArrayOutputStream.write(buffer);
            }

            else {
                break;
            }
        }

        soundTouch.close();

        return byteArrayOutputStream.toByteArray();
    }


}
