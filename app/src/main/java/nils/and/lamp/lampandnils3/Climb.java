package nils.and.lamp.lampandnils3;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by joe on 09/10/16.
 */

public class Climb {

    private BitmapDrawable photo;
    private String name;
    private String grade;
    private int length;
    private String description;

    public Climb(BitmapDrawable photo, String name, String grade, int length, String description) {
        this.photo = photo;
        this.name = name;
        this.grade = grade;
        this.length = length;
        this.description = description;
    }

    public Climb() {
    }

    public BitmapDrawable getPhoto() {
        return photo;
    }

    public void setPhoto(BitmapDrawable photo) {
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
