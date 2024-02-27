import java.rmi.*;
import java.rmi.server.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException; // Import IOException for handling file reading errors

// Import the MovieTicketService interface
import java.util.concurrent.locks.ReentrantLock;

class Movie {
    private String movieName;
    private String venue;
    private String time;
    private int cost;
    private boolean[] availableSeats;
    // private int price;

    public Movie(String movieName, String venue, String time, int cost) {
        this.movieName = movieName;
        this.venue = venue;
        this.time = time;
        this.cost = cost;
        this.availableSeats = new boolean[100];
        Arrays.fill(availableSeats, true);
    }

    public String getMovieName() {
        return movieName;
    }

    public String getVenue() {
        return venue;
    }

    public String getTime() {
        return time;
    }

    public int getCost() {
        return cost;
    }

    public boolean[] getAvailableSeats() {
        return availableSeats;
    }
}

public class MovieTicketServiceImpl extends UnicastRemoteObject implements MovieTicketService {
    private Map<String, Movie> movieMap; // Key: movie name, Value: Movie object
    private ReentrantLock lock; // For thread safety

    public MovieTicketServiceImpl() throws RemoteException {
        movieMap = new HashMap<>();
        lock = new ReentrantLock();
        loadMoviesFromFile("movie_data.csv");
    }

    private void loadMoviesFromFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // Extract movie data
                String movieName = parts[0].trim();
                String venue = parts[1].trim();
                String time = parts[2].trim();
                String cost = parts[3].trim();
                lock.lock(); // Lock to ensure thread safety while updating the movieMap
                System.out.println(movieName+" "+venue+" "+time+" "+cost);
                try {
                    movieMap.put(movieName, new Movie(movieName, venue, time, Integer.parseInt(cost)));
                    System.out.println("inserted into map");
                } finally {
                    lock.unlock(); // Ensure lock is released even if an exception occurs
                }
                
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error loading movies: " + e.getMessage());
        }
    }

    @Override
    public synchronized boolean bookTicket(String movieName, String venue, String time, int seatNumber) throws RemoteException {
        lock.lock(); // Lock to ensure thread safety while accessing movieMap
        try {
            // Find the movie by name
            Movie movie = findMovieByName(movieName);
            if (movie != null) {
                boolean[] availableSeats = movie.getAvailableSeats();
                if (seatNumber >= 1 && seatNumber <= availableSeats.length) {
                    if (availableSeats[seatNumber - 1]) {
                        availableSeats[seatNumber - 1] = false; // Mark the seat as booked
                        return true;
                    }
                }
            }
            return false; // Seat is not available or seat number is invalid
        } finally {
            lock.unlock(); // Ensure lock is released
        }
    }

    // Method to find the movie by name
    private Movie findMovieByName(String movieName) {
        System.out.println(movieMap.size());
        for (Map.Entry<String, Movie> entry : movieMap.entrySet()) {;
            if (entry.getKey().equalsIgnoreCase(movieName)) {
                System.out.println("\nFound movie!\n");
                return entry.getValue();
            }
        }
        System.out.println("\nNot Found movie!\n");
        return null; // Return null if movie is not found
    }

    public static void main(String[] args) {
        try {
            MovieTicketService stub = new MovieTicketServiceImpl();
            Naming.rebind("rmi://localhost:1099/MovieTicketService", stub);
            System.out.println("Server started...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
