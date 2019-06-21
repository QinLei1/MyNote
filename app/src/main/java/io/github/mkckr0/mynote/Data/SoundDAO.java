package io.github.mkckr0.mynote.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SoundDAO {
    @Query("select * from tb_sound where note_id=:note_id")
    public List<Sound> getAll(int note_id);

    @Query("select * from tb_sound where id=:id")
    public Sound get(int id);

    @Delete
    public void delete(Sound sound);

    @Delete
    public void delete(List<Sound> sounds);

    @Insert
    public void insert(Sound sound);

    @Insert
    public void insert(List<Sound> sounds);

    @Update
    public void update(List<Sound> sounds);

    @Query("select max(id) from tb_sound")
    public int getmaxid();
}
