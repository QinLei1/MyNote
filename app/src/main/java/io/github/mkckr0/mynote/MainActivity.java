package io.github.mkckr0.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.mkckr0.mynote.Data.AppDatabase;
import io.github.mkckr0.mynote.Data.Image;
import io.github.mkckr0.mynote.Data.Note;
import io.github.mkckr0.mynote.Data.Sound;
import io.github.mkckr0.mynote.DialogFragment.SoundPlayerDialogFragment;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mainActivity;
    private RecyclerView itemRecyclerView;
    private ItemRecyclerViewAdapter itemRecyclerViewAdapter;
    private AppDatabase appDatabase;
    public static CoordinatorLayout coordinatorLayout_snackbar;

    public static final String EXTRA_NOTE_POSITION = "io.github.mkckr0.mynote.EXTRA_NOTE_POSITION";
    public static final String EXTRA_NOTE = "io.github.mkckr0.mynote.EXTRA_NOTE";
    public static final String EXTRA_IMAGE_LIST = "io.github.mkckr0.mynote.EXTRA_IMAGE_LIST";
    public static final String EXTRA_SOUND_LIST = "io.github.mkckr0.mynote.EXTRA_SOUND_LIST";

    public static ArrayList<Note> mDataset = new ArrayList<>();
    public static ArrayList<ArrayList<Image>> mImageDataset = new ArrayList<>();
    public static ArrayList<ArrayList<Sound>> mSoundDataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        coordinatorLayout_snackbar = findViewById(R.id.coordinatorlayout);

        itemRecyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(mDataset, mImageDataset, mSoundDataset);
        itemRecyclerView.setAdapter(itemRecyclerViewAdapter);
        itemRecyclerView.addItemDecoration(new ItemRecyclerViewDecoration(20));

        (new Thread() {
            @Override
            public void run() {
                super.run();
                appDatabase = AppDatabase.getInstance();
                mDataset.clear();
                mImageDataset.clear();
                mSoundDataset.clear();
                mDataset.addAll(appDatabase.noteDAO().getAll());
                for (Note note : mDataset) {
                    ArrayList<Image> imageArrayList = new ArrayList<>();
                    imageArrayList.addAll(appDatabase.imageDAO().getAll(note.id));
                    mImageDataset.add(imageArrayList);
                    ArrayList<Sound> soundArrayList = new ArrayList<>();
                    soundArrayList.addAll(appDatabase.soundDAO().getAll(note.id));
                    mSoundDataset.add(soundArrayList);
                }
            }
        }).start();
    }

    //region 菜单处理
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_play:
                SoundPlayerDialogFragment soundPlayerDialogFragment = SoundPlayerDialogFragment.getInstance();
                soundPlayerDialogFragment.show(getSupportFragmentManager(), "play-sound");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    public void onClick_button_note(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_NOTE_POSITION, -1);
        intent.putExtra(EXTRA_NOTE, new Note());
        intent.putExtra(EXTRA_IMAGE_LIST, new ArrayList<Image>());
        intent.putExtra(EXTRA_SOUND_LIST, new ArrayList<Sound>());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int position = data.getIntExtra(EXTRA_NOTE_POSITION, -1);
        Note note = (Note) data.getSerializableExtra(EXTRA_NOTE);
        ArrayList<Image> imageArrayList = (ArrayList<Image>) data.getSerializableExtra(EXTRA_IMAGE_LIST);
        ArrayList<Sound> soundArrayList = (ArrayList<Sound>) data.getSerializableExtra(EXTRA_SOUND_LIST);
        if (position == -1) {
            itemRecyclerViewAdapter.addNote(note, imageArrayList, soundArrayList);
        } else {
            itemRecyclerViewAdapter.reviseNote(position, note, imageArrayList, soundArrayList);
        }
    }
}