package io.github.mkckr0.mynote.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDAO {
    @Query("select * from tb_image where note_id=:note_id")
    public List<Image> getAll(int note_id);

    @Query("select * from tb_image where id=:id")
    public Image get(int id);

    @Delete
    public void delete(Image image);

    @Delete
    public void delete(List<Image> images);

    @Insert
    public void insert(Image image);

    @Insert
    public void insert(List<Image> image);

    @Query("select max(id) from tb_image")
    public int getmaxid();

    @Update
    public void update(List<Image> images);

}
