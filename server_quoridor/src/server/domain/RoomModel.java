package server.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Valera on 28.01.2016.
 */

@DatabaseTable(tableName = "room")
public class RoomModel {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String title;

    @DatabaseField(columnName = "count_pl", defaultValue = "0")
    private Integer countPlayer;

    @DatabaseField
    private String status;

    public RoomModel() {
    }

    public RoomModel(String title, String status) {
        this.title = title;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCountPlayer() {
        return countPlayer;
    }

    public void setCountPlayer(Integer countPlayer) {
        this.countPlayer = countPlayer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoomModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", countPlayer=" + countPlayer +
                ", status='" + status + '\'' +
                '}';
    }
}
