package io.github.mkckr0.mynote;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.mkckr0.mynote.Data.AppDatabase;
import io.github.mkckr0.mynote.Data.Image;
import io.github.mkckr0.mynote.Data.Note;
import io.github.mkckr0.mynote.Data.Sound;
import io.github.mkckr0.mynote.DialogFragment.AddImageDialogFragment;
import io.github.mkckr0.mynote.DialogFragment.AddSoundDialogFragment;
import io.github.mkckr0.mynote.DialogFragment.PermissonDialogFragment;
import io.github.mkckr0.mynote.DialogFragment.RecorderDialogFragment;

public class EditActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CAMERA = 0;
    public static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    public static final int REQUEST_SETTING_PERMISSION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_RECORD_SOUND = 2;
    public static final int REQUEST_DRAW = 3;

    public static final String EXTRA_DRAWING_PATH = "io.github.mkckr0.mynote.EXTRA_DRAWING_PATH";

    private GridLayout gridLayout_image;
    private GridLayout gridLayout_sound;
    private TextView textView_time;

    private static int position;
    private static Note note;
    private static ArrayList<Image> imageArrayList;
    private static ArrayList<Sound> soundArrayList;
    private String imagePath;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        gridLayout_image = findViewById(R.id.gridLayout_edit_image);
        gridLayout_sound = findViewById(R.id.gridLayout_edit_sound);

        EditText editText_title = findViewById(R.id.editText_title);
        EditText editText_body = findViewById(R.id.editText_body);
        editText_title.clearFocus();
        editText_body.clearFocus();

        textView_time = findViewById(R.id.textView_time);

        Note xnote = (Note) getIntent().getSerializableExtra(MainActivity.EXTRA_NOTE);
        if (xnote != null) {
            note = xnote;
            position = getIntent().getIntExtra(MainActivity.EXTRA_NOTE_POSITION, -1);
            imageArrayList = (ArrayList<Image>) getIntent().getSerializableExtra(MainActivity.EXTRA_IMAGE_LIST);
            soundArrayList = (ArrayList<Sound>) getIntent().getSerializableExtra(MainActivity.EXTRA_SOUND_LIST);
        }

        editText_title.setText(note.title);
        editText_body.setText(note.body);
        Date date = note.timestamp == null ? new Date() : note.timestamp;
        textView_time.setText(new SimpleDateFormat("yyyy年MM月dd日").format(date));

        gridLayout_image.post(new Runnable() {
            @Override
            public void run() {
                for (Image image : imageArrayList) {
                    addImageToGridLayout(image.path);
                }
            }
        });

        gridLayout_sound.post(new Runnable() {
            @Override
            public void run() {
                for (Sound sound : soundArrayList) {
                    addSoundToGridLayout(sound.path);
                }
            }
        });
    }

    //region 功能按钮点击事件处理
    public void onClick_button_drawing(View view) {
        Intent intent = new Intent(this, DrawActivity.class);
        startActivityForResult(intent, REQUEST_DRAW);
    }

    public void onClick_button_image(View view) {
        AddImageDialogFragment addImageDialogFragment = new AddImageDialogFragment();
        addImageDialogFragment.show(getSupportFragmentManager(), "add-image");
    }

    public void onClick_button_sound(View view) {
        AddSoundDialogFragment addSoundDialogFragment = new AddSoundDialogFragment();
        addSoundDialogFragment.show(getSupportFragmentManager(), "add-sound");
    }
    //endregion

    //region 将媒体添加到控件
    public void addImageToGridLayout(String filepath) {
        Bitmap bitmap, thumbnail;
        ImageView imageView;
        int width = gridLayout_image.getWidth() / 3 - 10;
        int height;
        GridLayout.LayoutParams params;
        bitmap = BitmapFactory.decodeFile(filepath);
        height = bitmap.getWidth() / (bitmap.getWidth() / width);
        thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        imageView = new ImageView(gridLayout_image.getContext());
        imageView.setImageBitmap(thumbnail);
        params = new GridLayout.LayoutParams();
        params.rightMargin = 4;
        params.leftMargin = 4;
        params.topMargin = 4;
        params.bottomMargin = 4;
        gridLayout_image.addView(imageView, params);
    }

    public void addSoundToGridLayout(String filepath) {
        TextView textView = new TextView(gridLayout_sound.getContext());
        File file = new File(filepath);
        textView.setText(file.getName());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = gridLayout_sound.getWidth();
        params.height = 100;
        params.rightMargin = 4;
        params.leftMargin = 4;
        params.topMargin = 4;
        params.bottomMargin = 4;
        gridLayout_sound.addView(textView, params);
    }
    //endregion

    //region 媒体功能
    public void setPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, EditActivity.REQUEST_SETTING_PERMISSION);
    }

    public void takePhoto() {
        File file = Tool.createImage(this);
        Uri photoURI = Tool.getUri(this, file);
        if (photoURI == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        String photoPath = file.getAbsolutePath();
        imagePath = photoPath;
    }

    public void recordSound() {
        RecorderDialogFragment recorderDialogFragment = new RecorderDialogFragment();
        recorderDialogFragment.show(getSupportFragmentManager(), "recorder");
    }
    //endregion

    //region Activity回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_DRAW:
                if (resultCode == RESULT_OK) {
                    imagePath = data.getStringExtra(EXTRA_DRAWING_PATH);
                } else {
                    break;
                }
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    addImageToGridLayout(imagePath);
                    (new Thread() {
                        @Override
                        public void run() {
                            final Image image = new Image();
                            image.path = imagePath;
                            AppDatabase appDatabase = AppDatabase.getInstance();
                            if (position == -1) {
                                image.note_id = appDatabase.noteDAO().getmaxid() + 1;
                            } else {
                                image.note_id = note.id;
                            }
                            appDatabase.imageDAO().insert(image);
                            image.id = appDatabase.imageDAO().getmaxid();
                            imageArrayList.add(image);
                        }
                    }).start();
                } else {
                    imagePath = null;
                }
                break;
            case REQUEST_RECORD_SOUND:
                break;
            case REQUEST_SETTING_PERMISSION:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //endregion

    //region 权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    (new PermissonDialogFragment()).show(getSupportFragmentManager(), "request-permisson");
                }
                break;
            case PERMISSION_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordSound();
                } else {
                    (new PermissonDialogFragment()).show(getSupportFragmentManager(), "request-permisson");
                }
                break;
        }
    }
    //endregion

    public void addSoundToData(final String filename) {
        addSoundToGridLayout(filename);
        (new Thread() {
            @Override
            public void run() {
                Sound sound = new Sound();
                sound.path = filename;
                AppDatabase appDatabase = AppDatabase.getInstance();
                if (position == -1) {
                    sound.note_id = appDatabase.noteDAO().getmaxid() + 1;
                } else {
                    sound.note_id = note.id;
                }
                appDatabase.soundDAO().insert(sound);
                sound.note_id = appDatabase.soundDAO().getmaxid();
                soundArrayList.add(sound);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        saveAll();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveAll();
        finish();
        return true;
        //return super.onSupportNavigateUp();
    }

    private void saveAll() {
        EditText editText_title = findViewById(R.id.editText_title);
        EditText editText_body = findViewById(R.id.editText_body);

        note.title = editText_title.getText().toString().trim();
        note.body = editText_body.getText().toString().trim();

        getIntent().putExtra(MainActivity.EXTRA_NOTE_POSITION, position);
        getIntent().putExtra(MainActivity.EXTRA_NOTE, note);
        getIntent().putExtra(MainActivity.EXTRA_IMAGE_LIST, imageArrayList);
        getIntent().putExtra(MainActivity.EXTRA_SOUND_LIST, soundArrayList);
        setResult(RESULT_OK, getIntent());
    }

}
