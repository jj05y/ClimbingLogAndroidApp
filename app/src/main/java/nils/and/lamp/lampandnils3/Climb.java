package nils.and.lamp.lampandnils3;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by joe on 09/10/16.
 */

public class Climb {

    private Drawable photo;
    private String name;
    private String grade;
    private String length;
    private String description;

    public Climb(Drawable photo, String name, String grade, String length, String description) {
        this.photo = photo;
        this.name = name;
        this.grade = grade;
        this.length = length;
        this.description = description;
    }

    public Climb() {
    }

    public Drawable getPhoto() {
        return photo;
    }

    public void setPhoto(Drawable photo) {
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
}
