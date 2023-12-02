package com.salmane.mapxplorer.model;

import com.google.gson.annotations.SerializedName;

public class Location {
    private Long place_id;
    private Double lon;
    private Double lat;
    private String name;
    private String display_name;
    @SerializedName("class")
    private String _class;
    private String addresstype;
    private String type;
    private String[] boundingbox;
    private Address address;
    public class Address {
        private String highway;
        private String amenity;
        private String road;
        private String suburb;
        private String city;
        private String municipality;
        private String county;
        private String state_district;
        @SerializedName("ISO3166-2-lvl5")
        private String ISO_format_lvl5;
        @SerializedName("ISO3166-2-lvl4")
        private String ISO_format_lvl4;
        private String region;
        private String country;
        private String country_code;

        public String getMunicipality() {
            return municipality;
        }
        public String getCounty() {
            return county;
        }
        public String getState_district() {
            return state_district;
        }
        public String getISO_format_lvl5() {
            return ISO_format_lvl5;
        }
        public String getISO_format_lvl4() {
            return ISO_format_lvl4;
        }
        public String getRegion() {
            return region;
        }
        public String getCountry() {
            return country;
        }
        public String getCountry_code() {
            return country_code;
        }
    }

    public long getPlace_id() {
        return place_id;
    }
    public double getLon() {
        return lon;
    }
    public double getLat() {
        return lat;
    }
    public String getName() {
        return name;
    }
    public String getDisplay_name() {
        return display_name;
    }
    public String get_class() {
        return _class;
    }
    public String getAddresstype() {
        return addresstype;
    }
    public String getType() {
        return type;
    }
    public String[] getBoundingbox() {
        return boundingbox;
    }
    public Address getAddress() {
        return address;
    }
}
