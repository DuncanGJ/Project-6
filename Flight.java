import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Flight{
    //fields for flight manifest
    private Header header;
    private Seat seats[];
    private Passenger passengers[];
    private final Map<Seat.SeatId, Passenger> seatToPassenger = new HashMap<>();
    private final Map<Passenger.PassengerId, Seat.SeatId> passengerToSeat = new HashMap<>();

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
            Seat.fillSeats(header, seats);
            Scanner parsePassengers = new Scanner(data);
            parsePassengers.nextLine();
            int i = 0;
            while (parsePassengers.hasNextLine()){
                String line = parsePassengers.nextLine();
                passengers[i] = readPassenger(line);
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
}