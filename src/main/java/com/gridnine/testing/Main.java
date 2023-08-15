package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        System.out.println("Original list of flights:");
        printFlights(flights);

        System.out.println("\nFlights with departure before current time:");
        filterAndPrintFlights(flights,
                Main::isDepartureBeforeCurrentTime);

        System.out.println("\nFlights with segments having arrival before departure:");
        filterAndPrintFlights(flights,
                Main::isSegmentArrivalBeforeDeparture);

        System.out.println("\nFlights with total ground time exceeding two hours:");
        filterAndPrintFlights(flights,
                Main::isTotalGroundTimeExceedingTwoHours);
    }

    private static boolean isDepartureBeforeCurrentTime(Flight flight) {
        LocalDateTime currentTime = LocalDateTime.now();
        return flight.getSegments().stream().anyMatch(segment -> segment.getDepartureDate().isBefore(currentTime));
    }

    private static boolean isSegmentArrivalBeforeDeparture(Flight flight) {
        return flight.getSegments().stream().anyMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate()));
    }

    private static boolean isTotalGroundTimeExceedingTwoHours(Flight flight) {
        List<Segment> segments = flight.getSegments();
        for (int i = 1; i < segments.size(); i++) {
            LocalDateTime previousArrival = segments.get(i - 1).getArrivalDate();
            LocalDateTime currentDeparture = segments.get(i).getDepartureDate();
            if (Duration.between(previousArrival, currentDeparture).toHours() > 2) {
                return true;
            }
        }
        return false;
    }

    private static void filterAndPrintFlights(List<Flight> flights, FlightFilter filter) {
        List<Flight> filteredFlights = flights.stream().filter(filter::test).collect(Collectors.toList());
        printFlights(filteredFlights);
    }

    private static void printFlights(List<Flight> flights) {
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }

    interface FlightFilter {
        boolean test(Flight flight);
    }
}