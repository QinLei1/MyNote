package io.github.mkckr0.mynote.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "tb_note")
public class Note implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "order")
    public int order;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    public Note() {

    }

    @Ignore
    public Note(int order, String title, String body, Date timestamp) {
        this.order = order;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }
}
