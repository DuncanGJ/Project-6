import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        new test().menu(input);
    }

    private void menu(Scanner input){
        String fileName;
        System.out.println("please enter file name:");
        fileName = input.nextLine();
        Flight flight;
        try {
            flight = new Flight(fileName);
            String[] pName;
            String fName;
            String lName;
            Passenger passenger;
            int newRow;
            char newSeat;
        while (true){
            System.out.printf("%n");
            System.out.printf("~~~~~~~MENU~~~~~~~%n");
            System.out.printf("Please Type a Command:%n");
            System.out.printf("\"p\" to print passenger names and seat info%n");
            System.out.printf("\"m\" to modify a passenger %n");
            System.out.printf("\"s\" to modify a passenger's seat %n");
            System.out.printf("\"a\" add a new reservation %n");
            System.out.printf("\"c\" to cancel a reservation %n");
            System.out.printf("\"o\" to output current reservations to a file %n");
            System.out.printf("\"q\" to quit the program %n");
            String command = input.nextLine();
            switch (command) { 
                case "p":
                    flight.printAllData();
                    break;
                case "m":
                    System.out.printf("%n");
                    System.out.printf("Enter passenger name to modify:%n");
                    pName = input.nextLine().split(" ");
                    fName = pName[0];
                    lName = pName[1];
                    try {
                        passenger = selectPassenger(fName, lName, flight, input);}
                    catch(Exception e){ 
                        System.out.println("error malformed passenger selection: " + e);
                        break;  
                    }
                    System.out.printf("Please enter new passenger name:%n");
                    pName = input.nextLine().split(" ");
                    fName = pName[0];
                    lName = pName[1];
                    flight.modifyPassenger(passenger.getPassengerId(), fName, lName);
                    System.out.println("Modification completed!");
                    break;
                case "s":
                    System.out.printf("%n");
                    System.out.printf("Enter passenger name to modify:%n");
                    pName = input.nextLine().split(" ");
                    fName = pName[0];
                    lName = pName[1];
                    try {
                        passenger = selectPassenger(fName, lName, flight, input);}
                    catch(Exception e){ 
                        System.out.println("error malformed passenger selection: " + e);
                        break;  
                    }
                    Seat cSeat = flight.findSeat(passenger.getPassengerId());
                    System.out.println("Current Seat is: ");
                    cSeat.printSeat();
                    System.out.printf("Enter new passenger seat:%n");
                    newRow = input.nextInt();
                    newSeat = input.next().charAt(0);
                    input.nextLine();
                    flight.modifySeat(passenger.getPassengerId(), newRow, newSeat);
                    System.out.println("Seat Modified!");
                    break;
                case "a":
                    System.out.printf("%n");
                    System.out.printf("Enter passenger name to add:%n");
                    pName = input.nextLine().split(" ");
                    fName = pName[0];
                    lName = pName[1];
                    System.out.printf("Enter passenger Seat to add:%n");
                    newRow = input.nextInt();
                    newSeat = input.next().charAt(0);
                    input.nextLine();
                    flight.addReservation(fName, lName, newRow, newSeat);
                    System.out.println("Reservation Added!");
                    break;
                case "c":
                    System.out.printf("%n");
                    System.out.printf("Enter passenger name to remove:%n");
                    pName = input.nextLine().split(" ");
                    fName = pName[0];
                    lName = pName[1];
                    try {
                        passenger = selectPassenger(fName, lName, flight, input);}
                    catch(Exception e){ 
                        System.out.println("error malformed passenger selection: " + e);
                        break;  
                    }
                    flight.removeReservation(passenger.getPassengerId());
                    System.out.println("Passenger removed!");
                    break;
                case "o":
                    System.out.println("Please enter output filename: ");
                    String newOut = input.nextLine();
                    flight.writeToFile(newOut);
                    System.out.println("File writing complete!");
                    break;
                case "q":
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.printf("%n");
                    System.out.printf("INVALID COMMAND PLEASE TRY AGAIN.%n");
                    break;
                }
            }
        }  catch (Exception e) {
                System.out.println(e);
        }
    }
    private  Passenger selectPassenger(String fName, String lName, Flight flight, Scanner input){
        Passenger passengers[] = flight.findPassengers(fName, lName);
        System.out.println("We found the following passengers with that name");
        int i;
        for (i = 0; i <= passengers.length - 1; i++){
            System.out.printf("0%d %s %s %s%n",i, passengers[i].getFirstName(),passengers[i].getLastName(), passengers[i].getPassengerId());
        }
        System.out.printf("Please enter which passenger you'd like to select from 0 to %d%n", i-1);
        int selection = input.nextInt();
        input.nextLine(); 
        return passengers[selection];
    }
}


