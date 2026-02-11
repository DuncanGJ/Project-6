public final class Header {
    private final String flightNumber;
    private final int totalSeats;

    private final int firstClassRows;
    private final int firstClassSeats;
    private final int firstClassSeatsPerRow;

    private final int businessClassRows;
    private final int businessClassSeats;
    private final int businessClassSeatsPerRow;

    private final int coachRows;
    private final int coachSeats;
    private final int coachSeatsPerRow;


    //constructor
    public Header(
            String flightNumber,
            int totalSeats,
            int firstClassRows, int firstClassSeats,
            int businessClassRows, int businessClassSeats,
            int coachRows, int coachSeats
    ) {
        this.flightNumber = flightNumber;

        this.firstClassRows = firstClassRows;
        this.firstClassSeats = firstClassSeats;
        this.firstClassSeatsPerRow = seatsPerRowCheck("First Class", firstClassSeats, firstClassRows);

        this.businessClassRows = businessClassRows;
        this.businessClassSeats = businessClassSeats;
        this.businessClassSeatsPerRow = seatsPerRowCheck("Business Class", businessClassSeats, businessClassRows);

        this.coachRows = coachRows;
        this.coachSeats = coachSeats;
        this.coachSeatsPerRow = seatsPerRowCheck("Coach", coachSeats, coachRows);

        int expectedTotal = firstClassSeats + businessClassSeats + coachSeats;
        if (totalSeats != expectedTotal) {
            throw new IllegalArgumentException(
                    "totalSeats (" + totalSeats + ") must equal first + business + coach (" + expectedTotal + ")"
            );
        }
        this.totalSeats = totalSeats;
    }


    //Null/empty header constructor for manifests without a header.
    public Header() {
        this.flightNumber = "null";
        this.totalSeats = -1;

        this.firstClassRows = -1;
        this.firstClassSeats = -1;
        this.firstClassSeatsPerRow = -1;

        this.businessClassRows = -1;
        this.businessClassSeats = -1;
        this.businessClassSeatsPerRow = -1;

        this.coachRows = -1;
        this.coachSeats = -1;
        this.coachSeatsPerRow = -1;
    }

    private static int seatsPerRowCheck(String label, int seats, int rows) {
        if (rows <= 0) {
            throw new IllegalArgumentException(label + " rows must be > 0 (got " + rows + ")");
        }
        if (seats < 0) {
            throw new IllegalArgumentException(label + " seats must be >= 0 (got " + seats + ")");
        }
        if (seats % rows != 0) {
            throw new IllegalArgumentException(
                    label + " seats (" + seats + ") must be evenly divisible by rows (" + rows + ")"
            );
        }

        return seats / rows;
    }

    // Getters
    public String getFlightNumber() { return flightNumber; }
    public int getTotalSeats() { return totalSeats; }

    public int getFirstClassRows() { return firstClassRows; }
    public int getFirstClassSeats() { return firstClassSeats; }
    public int getFirstClassSeatsPerRow() { return firstClassSeatsPerRow; }

    public int getBusinessClassRows() { return businessClassRows; }
    public int getBusinessClassSeats() { return businessClassSeats; }
    public int getBusinessClassSeatsPerRow() { return businessClassSeatsPerRow; }

    public int getCoachRows() { return coachRows; }
    public int getCoachSeats() { return coachSeats; }
    public int getCoachSeatsPerRow() { return coachSeatsPerRow; }

    public void printHeader() {
        if (flightNumber.equals("null")) {
            System.out.println("Header is empty");
            return;
        }

        System.out.printf("~~~~Printing Flight %s Header~~~~%n", flightNumber);
        System.out.printf("Flight Number: %s%n", flightNumber);
        System.out.printf("Total Seats: %d%n", totalSeats);

        System.out.printf("First Class: rows=%d, seats=%d, seats per row=%d%n",
                firstClassRows, firstClassSeats, firstClassSeatsPerRow);
        System.out.printf("Business Class: rows=%d, seats=%d, seats per row=%d%n",
                businessClassRows, businessClassSeats, businessClassSeatsPerRow);
        System.out.printf("Coach: rows=%d, seats=%d, seats per row=%d%n",
                coachRows, coachSeats, coachSeatsPerRow);
    }
}
