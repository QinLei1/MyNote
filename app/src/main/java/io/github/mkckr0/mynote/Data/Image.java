package io.github.mkckr0.mynote.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "tb_image")
public class Image implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ForeignKey(entity = Note.class, onDelete = CASCADE, parentColumns = "id", childColumns = "note_id")
    @ColumnInfo(name = "note_id")
    public int note_id;

    @ColumnInfo(name = "path")
    public String path;

    public Image() {

    }

    @Ignore
    public Image(int note_id, String path) {
        this.note_id = note_id;
        this.path = path;
    }
}
