package nils.and.lamp.app.Core;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by joe on 09/10/16.
 */

public class Climb implements Serializable{

    private String photo;
    private String name;
    private String grade;
    private String length;
    private String description;
    private String gpsCoords;

    public Climb(String photo, String name, String grade, String length, String description, String gpsCoords) {
        this.photo = photo;
        this.name = name;
        this.grade = grade;
        this.length = length;
        this.description = description;
        this.gpsCoords = gpsCoords;
    }

    public Climb() {
    }

    public String getGpsCoords() {
        return gpsCoords;
    }

    public void setGpsCoords(String gpsCoords) {
        this.gpsCoords = gpsCoords;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Climb{" +
                ", name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", length='" + length + '\'' +
                ", description='" + description + '\'' +
//                ", photo='" + photo.toString() + '\'' +
                '}';
    }
}
