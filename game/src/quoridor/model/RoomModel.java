package quoridor.model;

/**
 * Created by Valera on 28.01.2016.
 */

public class RoomModel {

    private Integer id;

    private String title;

    private Integer countPlayer;

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
}
