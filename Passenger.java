public class Passenger {
    private String firstName;
    private String lastName;
    public record PassengerId(String id){}
    private final PassengerId id;

    //Constructor, sets UID with fname + lname + random 7 digit num combo
    public Passenger(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName= lastName;
        this.id = new PassengerId((String)firstName + lastName + (Math.random()*1000000));
    }

    //setters
    public void setFirstName(String firstName){ this.firstName = firstName; }
    public void setLasttName(String lastName){ this.lastName = lastName; }

    //getters
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public PassengerId getPassengerId(){return id;}

    
}
