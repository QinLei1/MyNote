package io.github.mkckr0.mynote.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "tb_sound")
public class Sound implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ForeignKey(entity = Note.class, onDelete = CASCADE, parentColumns = "id", childColumns = "note_id")
    @ColumnInfo(name = "note_id")
    public int note_id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "path")
    public String path;

    public Sound() {

    }

    @Ignore
    public Sound(int note_id, String title, String path) {
        this.note_id = note_id;
        this.title = title;
        this.path = path;
    }
}
