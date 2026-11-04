package cpar.backend.exception;

public class PositionOccupeeException extends RuntimeException {
    public PositionOccupeeException(int positionX, int positionY, String stationOccupante) {
        super("La case (" + positionX + ", " + positionY + ") est déjà occupée par la station « "
                + stationOccupante + " ».");
    }
}