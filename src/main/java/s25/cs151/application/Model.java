package s25.cs151.application;

import java.util.HashSet;
import java.util.Set;

public class Model {
    private String semester;
    private String year;
    private Set<String> selectedDays;
    private int selectedDaysCount;

    public Model() {
        selectedDays = new HashSet<>();
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Set<String> getSelectedDays() {
        return selectedDays;
    }

    public void addSelectedDay(String day) {
        selectedDays.add(day);
        selectedDaysCount++;
    }

    public void clearSelectedDays() {
        selectedDays.clear();
    }

    public int getSelectedDaysCount() {return selectedDaysCount;}
}
