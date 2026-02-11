import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Flight{
    //fields for flight manifest
    private Header header;
    private Seat seats[];
    private Passenger passengers[];
    private final Map<Seat.SeatId, Passenger> seatToPassenger = new HashMap<>();
    private final Map<Passenger.PassengerId, Seat.SeatId> passengerToSeat = new HashMap<>();
    private final Map<Seat.SeatId, Seat> seatMap = new HashMap<>();

    //Constructor
    public Flight(String fileName) throws IOException{
        //reads in data using file read helper method
        String data = getFile(fileName);
        
        //creates new header object by parsing data and then prints header
        //Header may not exist in file, if so null header object is created
        //null header object will prevent modification of flight manifest since seat quantities are not available
        this.header = readHeader(data);
        this.header.printHeader();

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
    private static void fillSeats(Header header, Seat[] seats) {
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
    
    //fill seatMap hash map helper method
    private void fillSeatMap(Seat[] seats){
        for(Seat s : seats){
            seatMap.put(s.id(), s);
        }
    }

    //read line to passenger arr helper method
    private static void readToPassenger(Passenger[] passengers, String line, int i){
        String[] parts = line.split(",");
        passengers[i] = new Passenger(parts[1], parts[2]);
        Seat.SeatId sID = new Seat.SeatId((Integer.parseInt(parts[3])), parts[4].charAt(0));
    }
}