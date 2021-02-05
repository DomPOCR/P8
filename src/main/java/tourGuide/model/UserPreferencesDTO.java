package tourGuide.model;

public class UserPreferencesDTO {

        private int attractionProximity;
        private String currency;
        private Double lowerPricePoint;
        private Double highPricePoint;
        private int tripDuration ;
        private int ticketQuantity ;
        private int numberOfAdults;
        private int numberOfChildren;
        private int numberOfProposalAttraction;

        public UserPreferencesDTO() {
        }

    public UserPreferencesDTO(int attractionProximity,
                              String currency,
                              Double lowerPricePoint,
                              Double highPricePoint,
                              int tripDuration,
                              int ticketQuantity,
                              int numberOfAdults,
                              int numberOfChildren,
                              int numberOfProposalAttraction) {

        this.attractionProximity = attractionProximity;
        this.currency = currency;
        this.lowerPricePoint = lowerPricePoint;
        this.highPricePoint = highPricePoint;
        this.tripDuration = tripDuration;
        this.ticketQuantity = ticketQuantity;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.numberOfProposalAttraction = numberOfProposalAttraction;
    }

    public int getAttractionProximity() {
        return attractionProximity;
    }

    public void setAttractionProximity(int attractionProximity) {
        this.attractionProximity = attractionProximity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getLowerPricePoint() {
        return lowerPricePoint;
    }

    public void setLowerPricePoint(Double lowerPricePoint) {
        this.lowerPricePoint = lowerPricePoint;
    }

    public Double getHighPricePoint() {
        return highPricePoint;
    }

    public void setHighPricePoint(Double highPricePoint) {
        this.highPricePoint = highPricePoint;
    }

    public int getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(int tripDuration) {
        this.tripDuration = tripDuration;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfProposalAttraction() {
        return numberOfProposalAttraction;
    }

    public void setNumberOfProposalAttraction(int numberOfProposalAttraction) {
        this.numberOfProposalAttraction = numberOfProposalAttraction;
    }

    @Override
    public String toString() {
        return "UserPreferencesDTO{" +
                "attractionProximity=" + attractionProximity +
                ", currency='" + currency + '\'' +
                ", lowerPricePoint=" + lowerPricePoint +
                ", highPricePoint=" + highPricePoint +
                ", tripDuration=" + tripDuration +
                ", ticketQuantity=" + ticketQuantity +
                ", numberOfAdults=" + numberOfAdults +
                ", numberOfChildren=" + numberOfChildren +
                ", numberOfProposalAttraction=" + numberOfProposalAttraction +
                '}';
    }
}
