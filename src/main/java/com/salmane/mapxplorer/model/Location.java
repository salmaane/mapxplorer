package com.salmane.mapxplorer.model;

public class Location {
    private String id;
    private String formattedAddress;
    private String shortFormattedAddress;
    private String[] types;
    private Coords location;
    private Viewport viewport;
    private Integer utcOffsetMinutes;
    private DisplayName displayName;
    private DisplayName primaryTypeDisplayName;
    private Photo[] photos;
    private Double rating;
    private Integer userRatingCount;
    private String websiteUri;
    private Boolean goodForChildren;
    private String nationalPhoneNumber;
    private String internationalPhoneNumber;
    private Route route;

    public class Coords {
        private Double longitude;
        private Double latitude;
        public Double getLongitude() {
            return longitude;
        }
        public Double getLatitude() {
            return latitude;
        }
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
    }
    public class Viewport {
        private Coords high;
        private Coords low;
        public Coords getHigh() {
            return high;
        }
        public Coords getLow() {
            return low;
        }
    }
    public class DisplayName {
        private String text;
        private String languageCode;
        public String getText() {
            return text;
        }
        public String getLanguageCode() {
            return languageCode;
        }
    }
    public class Photo {
        private String name;
        private Integer widthPx;
        private Integer heightPx;
        private Author[] authorAttributions;
        public String getName() {
            return name;
        }
        public Integer getWidthPx() {
            return widthPx;
        }
        public Integer getHeightPx() {
            return heightPx;
        }
        public Author[] getAuthorAttributions() {
            return authorAttributions;
        }
        public class Author {
            private String displayName;
            private String uri;
            private String photoUri;
            public String getDisplayName() {
                return displayName;
            }
            public String getUri() {
                return uri;
            }
            public String getPhotoUri() {
                return photoUri;
            }
        }
    }
    public String getId() {
        return id;
    }
    public String getFormattedAddress() {
        return formattedAddress;
    }
    public String getShortFormattedAddress() {
        return shortFormattedAddress;
    }
    public String[] getTypes() {
        return types;
    }
    public Coords getLocation() {
        return location;
    }
    public Viewport getViewport() {
        return viewport;
    }
    public Integer getUtcOffsetMinutes() {
        return utcOffsetMinutes;
    }
    public DisplayName getDisplayName() {
        return displayName;
    }
    public Photo[] getPhotos() {
        return photos;
    }
    public void setLocation(Coords location) {
        this.location = location;
    }
    public DisplayName getPrimaryTypeDisplayName() {
        return primaryTypeDisplayName;
    }
    public void setPrimaryTypeDisplayName(DisplayName primaryTypeDisplayName) {
        this.primaryTypeDisplayName = primaryTypeDisplayName;
    }
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
    public String getNationalPhoneNumber() {
        return nationalPhoneNumber;
    }
    public void setNationalPhoneNumber(String nationalPhoneNumber) {
        this.nationalPhoneNumber = nationalPhoneNumber;
    }
    public String getWebsiteUri() {
        return websiteUri;
    }
    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return displayName.getText();
    }
}
