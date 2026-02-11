import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Flight{
    //fields for flight manifest
    private int passengerCount = 0;
    private Header header;
    private Seat seats[];
    private Passenger passengers[];
    private final Map<Seat.SeatId, Passenger> seatToPassenger = new HashMap<>(); //hash map for managing occupied seats
    private final Map<Passenger.PassengerId, Seat.SeatId> passengerToSeat = new HashMap<>(); //hash map to quickly find passenger seat assignment
    private final Map<Seat.SeatId, Seat> seatMap = new HashMap<>(); //hash map to quickly check if seat exists 

    /////////////////////////////////////////////
    //Constructor + constructor helper methods//
    ///////////////////////////////////////////

    public Flight(String fileName) throws IOException{
        //reads in data using file read helper method
        String data = getFile(fileName);
        
        //creates new header object by parsing data and then prints header
        //Header may not exist in file, if so null header object is created
        //null header object will prevent modification of flight manifest since seat quantities are not available
        this.header = readHeader(data);
        this.header.printHeader();

        //fills seats arr and passengers arr with new passengers & seats
        //fills hash maps 
        if (!(this.header.getTotalSeats()==-1)){
            seats = new Seat[this.header.getTotalSeats()];
            passengers = new Passenger[this.header.getTotalSeats()];
            fillSeats(header, seats);
            Scanner parsePassengers = new Scanner(data);
            parsePassengers.nextLine();
            int i = 0;
            while (parsePassengers.hasNextLine()){
                String line = parsePassengers.nextLine();
                readToPassenger(passengers, line, i);
                i++;
                if (i>= passengers.length){throw new IllegalArgumentException("Passengers exceed seat count");}
                passengerCount++;
            }
            parsePassengers.close();
        }

    }

    //File read helper method 
    private static String getFile(String filename) throws IOException {
        String path = filename;
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\\Z");
        String content = scanner.next();
        scanner.close();
        return content; 
    }

    //Header read helper method
    //Can throw error on invalid header construction
    private static Header readHeader(String data){
        String flightNumber;
        int totalSeats;

        int firstClassSeats;
        int firstClassRows;

        int businessClassSeats;
        int businessClassRows;

        int coachSeats;
        int coachRows;

        Header localHeader;
        Scanner scanner = new Scanner(data);
        String[] headerParts = scanner.nextLine().split(",");
        if (headerParts[0].equals("HEADER")) {
            flightNumber = headerParts[1].trim();
            totalSeats = Integer.parseInt(headerParts[2].trim());

            firstClassSeats = Integer.parseInt(headerParts[3].trim());
            firstClassRows  = Integer.parseInt(headerParts[4].trim());

            businessClassSeats = Integer.parseInt(headerParts[5].trim());
            businessClassRows  = Integer.parseInt(headerParts[6].trim());

            coachSeats = Integer.parseInt(headerParts[7].trim());
            coachRows  = Integer.parseInt(headerParts[8].trim());
            try {
                localHeader = new Header(
                        flightNumber,
                        totalSeats,
                        firstClassRows, firstClassSeats,
                        businessClassRows, businessClassSeats,
                        coachRows, coachSeats
                );
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid HEADER line: " + e.getMessage());
                throw e;
            }finally{ scanner.close();}
        } else {
            localHeader = new Header();
        }
         return localHeader;
    }

    //fill seats helper method
    private void fillSeats(Header header, Seat[] seats) {
        int idx = 0;
        int nextRow = 1;

        idx = fillCabin(
                seats, idx, nextRow,
                header.getFirstClassRows(),
                header.getFirstClassSeatsPerRow(),
                Seat.CabinClass.FIRST
        );
        nextRow += header.getFirstClassRows();

        idx = fillCabin(
                seats, idx, nextRow,
                header.getBusinessClassRows(),
                header.getBusinessClassSeatsPerRow(),
                Seat.CabinClass.BUSINESS
        );

        nextRow += header.getBusinessClassRows();

        idx = fillCabin(
                seats, idx, nextRow,
                header.getCoachRows(),
                header.getCoachSeatsPerRow(),
                Seat.CabinClass.COACH
        );
        fillSeatMap(seats);
    }

    //fill seats helper helper method 
    private static int fillCabin(
            Seat[] seats,
            int startIndex,
            int startRowNumber,
            int rows,
            int seatsPerRow,
            Seat.CabinClass cabin
    ) {
        int idx = startIndex;

        for (int r = 0; r < rows; r++) {
            int rowNumber = startRowNumber + r;

            for (int s = 0; s < seatsPerRow; s++) {
                char letter = (char) ('A' + s);
                seats[idx++] = new Seat(new Seat.SeatId(rowNumber, letter), cabin);
            }
        }
        return idx;
    }

    //read line to passenger arr helper method
    private void readToPassenger(Passenger[] passengers, String line, int i){
        String[] parts = line.split(",");
        passengers[i] = new Passenger(parts[0], parts[1]);
        Seat.SeatId sId = new Seat.SeatId((Integer.parseInt(parts[2])), parts[3].charAt(0));
        assignSeat(sId, passengers[i].getPassengerId(), passengers[i]);
    }

    ////////////////////////////
    //Hash map helper methods//
    //////////////////////////

    //fill seatMap hash map helper method
    private void fillSeatMap(Seat[] seats){
        for(Seat s : seats){
            seatMap.put(s.id(), s);
        }
    }

    //checks if seat exists, then assigns seat to the respective hash maps
    private Boolean assignSeat(Seat.SeatId sId, Passenger.PassengerId pId, Passenger passenger){
        if(!seatMap.containsKey(sId)){
            throw new IllegalArgumentException("Seat does not exist" + sId);
        }

        if(passengerToSeat.putIfAbsent(pId, sId) != null){
            throw new IllegalArgumentException("Seat already occupied" + sId);
        }

        if(seatToPassenger.putIfAbsent(sId, passenger) != null){
            throw new IllegalArgumentException("Seat already occupied" + sId);
        }
        return true;
    }

    //removes seat assignment from both hash maps, do not use hashmap.remove() elsewhere 
    private Boolean removeSeat(Seat.SeatId sId, Passenger.PassengerId pId, Passenger passenger){
        if(!seatMap.containsKey(sId)){
            throw new IllegalArgumentException("Seat does not exist" + sId);
        }

        if (!passengerToSeat.remove(pId, sId)){
            throw new IllegalArgumentException("Passenger not sitting at this seat" + sId);
        }
        
        if (!seatToPassenger.remove(sId, passenger)){
            throw new IllegalArgumentException("Passenger not sitting at this seat" + sId);
        }
        return true;
    }

    //find seat based on passenger ID
    public Seat findSeat(Passenger.PassengerId pId){
        Seat.SeatId sId = passengerToSeat.get(pId);
        return seatMap.get(sId); 
    }


    //////////////////////////////
    //Flight operations methods//
    ////////////////////////////
    
    public void addReservation(String firstName, String lastName, int rowNumber, char seat){
        Seat.SeatId sId = new Seat.SeatId(rowNumber, seat);
        Passenger passenger = new Passenger(firstName, lastName);
        if (assignSeat(sId, passenger.getPassengerId(), passenger)){
                passengers[passengerCount] = passenger;
                passengerCount++;
            }
    }

    public void modifySeat(Passenger.PassengerId pId, int rowNumber, char seat){
        Seat.SeatId sId = passengerToSeat.get(pId);
        Passenger passenger = seatToPassenger.get(sId);
        removeSeat(sId, pId, passenger);
        sId = new Seat.SeatId(rowNumber, seat);
        assignSeat(sId, pId, passenger);
    }   

    public void modifyPassenger(Passenger.PassengerId pId, String fName, String lName){
        Passenger p;
        for(int i = 0; i <= passengerCount-1; i++){
            p = passengers[i];
            if (pId.equals(p.getPassengerId())){
                p.setFirstName(fName);
                p.setLasttName(lName);
            }
        }
    }

    public void removeReservation(Passenger.PassengerId pId){
        Seat.SeatId sId = passengerToSeat.get(pId);
        Passenger passenger = seatToPassenger.get(sId);
        removeSeat(sId, pId, passenger);
        for(int i = 0; i <= passengerCount-1; i++){
            if(passengers[i].getPassengerId().equals(pId)){
                Passenger temp = passengers[i];
                passengers[i] = passengers[passengerCount-1];
                passengers[passengerCount-1] = null;
                passengerCount--;
            }
        }
    }

    //returns an array of all passenger Ids matching the first & last name provided
    public Passenger[] findPassengers(String firstName, String lastName){
        String oCompare = firstName + lastName;
        Passenger[] rPassengers = new Passenger[header.getTotalSeats()];
        int tempArrSize = 0;
        for (int i = 0; i <= passengerCount-1; i++){
            Passenger p = passengers[i];
            String iCompare = p.getFirstName() + p.getLastName();
            if(oCompare.equals(iCompare)){
                rPassengers[tempArrSize] = p;
                tempArrSize++;
            }
        }
        return java.util.Arrays.copyOf(rPassengers, tempArrSize);
    }

//prints all data into the console
public void printAllData() {
    System.out.println("==== MANIFEST DUMP ====");

    System.out.println();
    header.printHeader();

    System.out.println();
    printPassengers();

    System.out.println();
    printSeatAssignments();
}

//helper method for printing to console 
private void printPassengers() {
    System.out.printf("---- PASSENGERS (%d) ----%n", passengerCount);
    for (int i = 0; i < passengerCount; i++) {
        Passenger p = passengers[i];
        System.out.printf("[%03d] %s%n", i, p); // relies on Passenger.toString()
    }
}

//helper method for printing to console 
private void printSeatAssignments() {
    System.out.println("---- SEATS / OCCUPANCY ----");
    for (Seat s : seats) {
        Passenger occ = seatToPassenger.get(s.id()); // null means empty
        if (occ == null) {
            System.out.printf("%s %-8s : EMPTY%n", formatSeatId(s.id()), s.cabinClass());
        } else {
            System.out.printf("%s %-8s : %s%n", formatSeatId(s.id()), s.cabinClass(), occ);
        }
    }
}

//helper method for printing to console 
private static String formatSeatId(Seat.SeatId id) {
    return id.row() + String.valueOf(id.letter());
}

public void writeToFile(String outputFileName) throws IOException {
    try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {

        out.printf("HEADER,%s,%d,%d,%d,%d,%d,%d,%d%n",
                header.getFlightNumber(),
                header.getTotalSeats(),
                header.getFirstClassSeats(),
                header.getFirstClassRows(),
                header.getBusinessClassSeats(),
                header.getBusinessClassRows(),
                header.getCoachSeats(),
                header.getCoachRows()
        );

        for (Seat seat : seats) {
            Passenger p = seatToPassenger.get(seat.id());
            if (p == null) continue;

            Seat.SeatId sid = seat.id();
            out.printf("%s,%s,%d,%c%n",
                    p.getFirstName(),
                    p.getLastName(),
                    sid.row(),
                    sid.letter()
            );
        }
    }
}


}