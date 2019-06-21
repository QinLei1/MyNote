package io.github.mkckr0.mynote.Data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import io.github.mkckr0.mynote.BaseApp;

@Database(entities = {Note.class, Sound.class, Image.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "db_note.db";
    private static AppDatabase ourInstance = null;

    public abstract NoteDAO noteDAO();

    public abstract SoundDAO soundDAO();

    public abstract ImageDAO imageDAO();

    public static AppDatabase getInstance() {
        if (ourInstance == null) {
            synchronized (AppDatabase.class) {
                if (ourInstance == null) {
                    ourInstance = Room.databaseBuilder(BaseApp.getContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return ourInstance;
    }

    @Override
    public void close() {
        super.close();
        ourInstance = null;
    }
}
