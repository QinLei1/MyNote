package io.github.mkckr0.mynote.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.mkckr0.mynote.EditActivity;
import io.github.mkckr0.mynote.R;
import io.github.mkckr0.mynote.Tool;

public class RecorderDialogFragment extends DialogFragment {

    private ImageButton recorder_control;
    private Button recorder_cancel;
    private Button recorder_save;
    private Chronometer recorder_time;

    private AudioRecord audioRecord;
    private int bufferSizeInBytes;
    private boolean isRecording;
    private File soundFile;

    private static final int sampleRateInHz = 44100;
    private static final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int audioSource = MediaRecorder.AudioSource.MIC;

    private Thread writeSoundFileThread = new Thread() {
        @Override
        public void run() {
            FileOutputStream out;
            try {
                out = new FileOutputStream(soundFile, false);
                out.write(new byte[44], 0, 44);

                byte[] audioData = new byte[bufferSizeInBytes];
                while (isRecording) {
                    audioRecord.read(audioData, 0, bufferSizeInBytes);
                    out.write(audioData);
                }

                int NumChannel = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
                out.getChannel().position(0);
                Tool.writeWaveFileHeader(out, sampleRateInHz, NumChannel, 16);
                out.close();

                try {
                    FileInputStream in = new FileInputStream(soundFile);
                    byte[] bytes = new byte[44];
                    in.read(bytes);
                    for (int i = 0; i < 44; i++) {
                        if (i % 4 == 0) {
                            System.out.println();
                        }
                        System.out.print(bytes[i]);
                        System.out.print(" ");
                    }
                    System.out.println();
                    in.close();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
                dismiss();
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_recorder, null);
        recorder_control = view.findViewById(R.id.recorder_control);
        recorder_cancel = view.findViewById(R.id.recorder_cancel);
        recorder_save = view.findViewById(R.id.recorder_save);
        recorder_time = view.findViewById(R.id.recorder_time);
        recorder_cancel.setOnClickListener(new OnClickListener_Cancel());
        recorder_control.setOnClickListener(new OnClickListener_Control());
        recorder_save.setOnClickListener(new OnClickListener_Save());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        recorder_control.getDrawable().setTint(getResources().getColor(android.R.color.holo_green_dark, getActivity().getTheme()));
        isRecording = false;

        soundFile = Tool.createFile(getActivity(), "WAV", ".wav", "sound");
        if (soundFile == null) {
            Toast.makeText(getActivity(), "无法创建文件", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return dialog;
    }

    private void startRecording() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            audioRecord = null;
            Toast.makeText(getActivity(), "无法初始化AudioRecorder", Toast.LENGTH_SHORT);
            dismiss();
        }

        audioRecord.startRecording();
        isRecording = true;
        writeSoundFileThread.start();
        recorder_control.getDrawable().setTint(getResources().getColor(android.R.color.holo_red_dark, getActivity().getTheme()));
        recorder_time.setBase(SystemClock.elapsedRealtime());
        recorder_time.start();
    }

    private void stopRecording() {
        isRecording = false;
        try {
            writeSoundFileThread.join(500);
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        recorder_control.getDrawable().setTint(getResources().getColor(android.R.color.holo_green_dark, getActivity().getTheme()));
        recorder_time.stop();
    }

    class OnClickListener_Control implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        }
    }

    class OnClickListener_Save implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isRecording) {
                stopRecording();
            }
            try {
                writeSoundFileThread.join(500);
            } catch (InterruptedException e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            ((EditActivity) getActivity()).addSoundToData(soundFile.getAbsolutePath());
            dismiss();
        }
    }

    class OnClickListener_Cancel implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isRecording) {
                stopRecording();
            }
            try {
                writeSoundFileThread.join(500);
            } catch (InterruptedException e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            soundFile.delete();
            dismiss();
        }
    }
}
