import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MovieTicketService extends Remote {
    boolean bookTicket(String movieName, String venue, String time, int seatNumber) throws RemoteException;
}
