package app.com.catapp;

import java.io.Serializable;

public class Breed implements Serializable{
    public String id;
    public String name;
    public String description;
    public String image = "";
    public String temperament;
    public String origin;
    public String life_span;
    public String wikipedia_url;
    public String dog_friendly;
    public String weightImperial;
    public String weightMetric;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Breed){
            Breed other = (Breed) obj;
            return  id.equals(other.id);
        }
        return super.equals(obj);
    }

}
