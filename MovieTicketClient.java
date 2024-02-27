import java.rmi.Naming;
import java.util.Scanner;

public class MovieTicketClient {
    public static void main(String[] args) {
        try {
            // Look up the remote MovieTicketService object
            MovieTicketService ticketService = (MovieTicketService) Naming.lookup("rmi://localhost:1099/MovieTicketService");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                // Get user input for movie details
                System.out.print("Enter movie name: ");
                String movieName = scanner.nextLine();
                System.out.print("Enter venue: ");
                String venue = scanner.nextLine();
                System.out.print("Enter time: ");
                String time = scanner.nextLine();
                System.out.print("Enter seat number: ");
                int seatNumber = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
                System.out.println("\nMovie request from client of: "+movieName+" at "+venue+" on time: "+time+" with seat: "+seatNumber+"\n");
                // Attempt to book the ticket
                boolean success = ticketService.bookTicket(movieName, venue, time, seatNumber);
                if (success) {
                    System.out.println("Ticket booked successfully!");
                } else {
                    System.out.println("Sorry, the seat is already booked or unavailable. Please try a different one.");
                }
                // Ask user if they want to book another ticket
                System.out.print("Do you want to book another ticket? (yes/no): ");
                String choice = scanner.nextLine();
                if (!choice.equalsIgnoreCase("yes")) {
                    break;
                }
            }
            // Close scanner
            scanner.close();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

