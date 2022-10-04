package com.example.insulinapp;

/* A basic custom class to hold the information for our api interactions. This can certainly be
expanded more and be more robust with what the user is shown from their searches. At this iteration
of this app however, the only thing that is needed is the nf_total_carbohydrate variable and the
 respective get function. */
public class Quote {
    private String food_name;
    private String brand_name;
    private String serving_weight_grams;
    private String nf_total_carbohydrate;

    public Quote(  String food_name, String brand_name, String serving_weight_grams, String nf_total_carbohydrate) {

        this.food_name = food_name;
        this.brand_name = brand_name;
        this.serving_weight_grams = serving_weight_grams;
        this.nf_total_carbohydrate = nf_total_carbohydrate;


    }

    public String getName() {
        return food_name;
    }
    public String getBrand_name() {return brand_name;}
    public String getServing_weight_grams() {
        return serving_weight_grams;
    }

    public String getNf_total_carbohydrate() {
        return nf_total_carbohydrate;
    }

}


