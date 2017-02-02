package com.vladimirlogachov.mvi.sample.departments.model;

public class DepartmentEmployee implements PayloadItem {
    private int age;
    private String country;
    private String name;
    private String workplace;

    public DepartmentEmployee() {
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getWorkplace() {
        return workplace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentEmployee)) return false;

        DepartmentEmployee that = (DepartmentEmployee) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DepartmentEmployee{" +
                "age=" + age +
                ", country='" + country + '\'' +
                ", name='" + name + '\'' +
                ", workplace='" + workplace + '\'' +
                '}';
    }
}
