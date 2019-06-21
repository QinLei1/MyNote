package io.github.mkckr0.mynote.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.mkckr0.mynote.R;
import io.github.mkckr0.mynote.Tool;

public class SoundPlayerDialogFragment extends DialogFragment {
    public static final SoundPlayerDialogFragment soundPlayerDialogFragment = new SoundPlayerDialogFragment();

    private Dialog dialog = null;
    private ImageView sound_player_image;
    private TextView sound_player_title;
    private SeekBar sound_player_progress;
    private TextView sound_player_time;
    private ImageButton sound_player_play;
    private ImageButton sound_player_stop;

    private boolean isStart;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private String duration;
    private final long period = 200;

    Handler timer = new Handler();
    Runnable onTimer = new Runnable() {
        @Override
        public void run() {
            int pos = mediaPlayer.getCurrentPosition();
            sound_player_progress.setProgress(pos);
            sound_player_time.setText(new SimpleDateFormat("mm:ss").format(pos) + "/" + duration);
            timer.postDelayed(this, period);
        }
    };

    private SoundPlayerDialogFragment() {
        super();
    }

    public static SoundPlayerDialogFragment getInstance() {
        return soundPlayerDialogFragment;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        this.dialog.hide();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dialog != null) {
            return dialog;
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_sound_player, null);
        sound_player_image = view.findViewById(R.id.sound_player_image);
        sound_player_title = view.findViewById(R.id.sound_player_title);
        sound_player_progress = view.findViewById(R.id.sound_player_progress);
        sound_player_time = view.findViewById(R.id.sound_player_time);
        sound_player_play = view.findViewById(R.id.sound_player_play);
        sound_player_stop = view.findViewById(R.id.sound_player_stop);

        setSoundMetadata(null);

        sound_player_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b == false) {
                    return;
                }
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sound_player_play.setOnClickListener(new OnClickListener_control());
        sound_player_stop.setOnClickListener(new OnClickListener_cancel());
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        dialog = builder.create();

        isStart = false;
        isPlaying = false;

        return dialog;
    }

    class OnClickListener_control implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (isStart) {
                if (isPlaying) {
                    pausePlaying();
                } else {
                    resumePlaying();
                }
            } else {
                startPlaySound("2.flac");
            }
        }
    }

    class OnClickListener_cancel implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (mediaPlayer != null) {
                stopPlaySound();
            }
        }
    }

    private void setSoundMetadata(String filename) {
        if (filename == null) {
            sound_player_image.setImageResource(android.R.color.holo_blue_dark);
            sound_player_title.setText("");
            sound_player_time.setText("00:00/00:00");
            sound_player_progress.setMax(-1);
        } else {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/sound/" + filename);
            byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
            Bitmap albumCover = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            sound_player_image.setImageBitmap(albumCover);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            sound_player_title.setText(title + " - " + artist);
            duration = new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getDuration()));
            sound_player_time.setText("00:00/" + duration);
            sound_player_progress.setMax(mediaPlayer.getDuration());
            mediaMetadataRetriever.release();
        }
    }

    private void startPlaySound(String filename) {
        File file = new File(getActivity().getExternalFilesDir(null).getAbsoluteFile() + "/sound/" + filename);
        Uri soundUri = Tool.getUri(getActivity(), file);
        if (soundUri == null) {
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlaySound();
                }
            });
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), soundUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        isStart = true;
        isPlaying = true;
        sound_player_play.setImageResource(android.R.drawable.ic_media_pause);
        sound_player_progress.setMax(mediaPlayer.getDuration());
        timer.postDelayed(onTimer, period);
        setSoundMetadata(filename);
    }

    private void stopPlaySound() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        isStart = false;
        isPlaying = false;
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);
        timer.removeCallbacks(onTimer);
        setSoundMetadata(null);
    }

    private void pausePlaying() {
        mediaPlayer.pause();
        isPlaying = false;
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);
        timer.removeCallbacks(onTimer);
    }

    private void resumePlaying() {
        mediaPlayer.start();
        isPlaying = true;
        sound_player_play.setImageResource(android.R.drawable.ic_media_pause);
        timer.postDelayed(onTimer, period);
    }
}
