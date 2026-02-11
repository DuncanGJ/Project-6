public class Seat {
    public enum CabinClass { COACH, BUSINESS, FIRST }
    public record SeatId(int row, char letter) {}

    private final SeatId id;
    private final CabinClass cabinClass;

    public Seat(SeatId id, CabinClass cabinClass){
        this.id = id;
        this.cabinClass = cabinClass;
    }

    public SeatId id() { return id; }
    public CabinClass cabinClass() { return cabinClass; }

    public void printSeat(){
        System.out.printf("%s %s%n", id, cabinClass);
    }

}
