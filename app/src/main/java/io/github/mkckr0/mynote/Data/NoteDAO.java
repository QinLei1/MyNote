package io.github.mkckr0.mynote.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("select * from tb_note order by `order` desc")
    public List<Note> getAll();

    @Query("select * from tb_note where id=:nid")
    public Note get(int nid);

    @Update
    public void update(Note note);

    @Update
    public void update(List<Note> note);

    @Insert
    public void insert(Note note);

    @Delete
    public void delete(Note note);

    @Query("select max(id) from tb_note")
    public int getmaxid();

}
