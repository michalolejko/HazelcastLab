import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static HazelcastInstance hazelcastInstance;
    static Config config;
    static Scanner scanner;
    static Map<Long, Course> courses;

    public static void main(String[] args) throws IOException {
        hazelcastInstance = Hazelcast.newHazelcastInstance();
        config = HConfig.getConfig();
        courses = hazelcastInstance.getMap("courses");
        scanner = new Scanner(System.in);
        while (true) {
            showMenu();
            switch (scanner.nextInt()) {
                case 0:
                    hazelcastInstance.shutdown();
                    System.out.println("Zakonczono");
                    return;
                case 1:
                    putNewCourse();
                    break;
                case 2:
                    updateCourse();
                    break;
                case 3:
                    deleteCourse();
                    break;
                case 4:
                    showCourseById();
                    break;
                case 5:
                    advancedDownload();
                    break;
                case 6:
                    processing();
                    break;
            }
        }
    }

    static void showMenu() {
        System.out.print("\n2) Firma przewozowa\n\nWybierz operacje:\n" +
                "1.Zapisywanie\n2.Aktualizowanie\n3.Kasowanie\n4.Pobieranie po ID\n5.Pobieranie (inne)\n" +
                "6.Przetwarzanie(Zmien wszystkie kursy)\n0.Zakoncz\n\nWpisz cyfre i zatwierdz enterem: ");
    }

    static void processing() {
        scanner.nextLine();
        String oldDeparture, newDeparture;
        int hour;
        System.out.println("Zmiana wszystkich miejsc wyjazdu powyzej danej godziny:");
        System.out.println("Podaj stare miejsce wyjazdu:");
        oldDeparture = scanner.nextLine();
        System.out.println("Podaj nowe miejsce wyjazdu:");
        newDeparture = scanner.nextLine();
        System.out.println("Od godziny: ");
        hour = scanner.nextInt();
        for (Map.Entry<Long, Course> e : courses.entrySet())
            if (e.getValue().getHour() >= hour && e.getValue().getDeparture().equals(oldDeparture)) {
                e.getValue().setDeparture(newDeparture);
                courses.put(e.getKey(), e.getValue());
            }
    }

    static void advancedDownload() throws IOException {
        System.out.println("Wybierz:\n1. Aby szukac po miejscu wyjazdu\n2. Aby szukac po miejscu docelowym\n3. Aby szukac po godzinach kursow\n4. Aby zobaczyc wszystkie kursy");
        switch (scanner.nextInt()) {
            case 1:
                showCourseByDeparture();
                break;
            case 2:
                showCourseByDestination();
                break;
            case 3:
                showCourseByHour();
                break;
            case 4:
                showListOfCourses();
            default:
                return;
        }
        System.out.println("Wcisnij enter");
        System.in.read();
    }

    static void showListOfCourses() {
        System.out.println("Wszystkie kursy: ");
        for (Map.Entry<Long, Course> e : courses.entrySet())
            System.out.println(e.getValue());
    }

    static void showCourseByDestination() {
        String destination;
        scanner.nextLine();
        System.out.println("Podaj szukane miejsce docelowe:");
        destination = scanner.nextLine();
        System.out.println("Znaleziono ponizsze kursy:");
        for (Map.Entry<Long, Course> e : courses.entrySet())
            if (e.getValue().getDestination().equals(destination))
                System.out.println(e.getValue());
    }

    static void showCourseByHour() {
        int hour;
        System.out.println("Podaj szukana godzine:");
        hour = scanner.nextInt();
        System.out.println("Znaleziono ponizsze kursy:");
        for (Map.Entry<Long, Course> e : courses.entrySet())
            if (e.getValue().getHour() == hour)
                System.out.println(e.getValue());
    }

    static void showCourseByDeparture() {
        String departure;
        scanner.nextLine();
        System.out.println("Podaj szukane miejsce wyjazdu:");
        departure = scanner.nextLine();
        System.out.println("Znaleziono ponizsze kursy:");
        for (Map.Entry<Long, Course> e : courses.entrySet())
            if (e.getValue().getDeparture().equals(departure))
                System.out.println(e.getValue());
    }

    static void showCourseById() throws IOException {
        long key;
        int tmp;
        System.out.println("Podaj ID kursu: ");
        while (true) {
            key = scanner.nextLong();
            if (key < 0)
                System.out.println("Klucz nie moze byc ujemny");
            else break;
        }
        if (courses.containsKey(key))
            System.out.println(courses.get(key));
        else
            System.out.println("Nie znaleziono takiego kursu");
    }

    static void deleteCourse() throws IOException {
        Map<Integer, Long> decode = new HashMap<>();
        int counter = 1;
        System.out.println("Wybierz kurs: ");
        for (Map.Entry<Long, Course> e : courses.entrySet()) {
            System.out.println(counter + ". klucz: " + e.getKey() + " => " + e.getValue());
            decode.put(counter++, e.getKey());
        }
        if (counter < 2) {
            System.out.println("Niestety nie ma jeszcze zadnego kursu :(\nWcisnij enter aby kontunuować...");
            System.in.read();
            return;
        }
        int decodedKey = -1;
        while (true) {
            decodedKey = scanner.nextInt();
            if (decodedKey < 1 || decodedKey > counter - 1)
                System.out.println("Podaj wartosc pomiedzy 1, a " + (counter - 1));
            else break;
        }
        System.out.println("DELETED " + decode.get(decodedKey) + " =>" + courses.get(decode.get(decodedKey)));
        courses.remove(decode.get(decodedKey));
        System.out.println("Wcisnij enter");
        System.in.read();
    }

    static void updateCourse() throws IOException {
        Map<Integer, Long> decode = new HashMap<>();
        int counter = 1;
        System.out.println("Wybierz kurs: ");
        for (Map.Entry<Long, Course> e : courses.entrySet()) {
            System.out.println(counter + ". klucz: " + e.getKey() + " => " + e.getValue());
            decode.put(counter++, e.getKey());
        }
        if (counter < 2) {
            System.out.println("Niestety nie ma jeszcze zadnego kursu :(\nWcisnij enter aby kontunuować...");
            System.in.read();
            return;
        }

        String destination, departure;
        int hour = -1;
        int decodedKey = -1;
        while (true) {
            decodedKey = scanner.nextInt();
            if (decodedKey < 1 || decodedKey > counter - 1)
                System.out.println("Podaj wartosc pomiedzy 1, a " + (counter - 1));
            else break;
        }

        scanner.nextLine();
        System.out.println("Podaj miejsce wyjazdu (lub enter, żeby zostawić poprzednią wartość (" + courses.get(decode.get(decodedKey)).getDeparture() + "):");
        departure = scanner.nextLine();
        if (departure.isEmpty())
            departure = courses.get(decode.get(decodedKey)).getDeparture();

        System.out.println("Podaj miejsce docelowe (lub enter, żeby zostawić poprzednią wartość (" + courses.get(decode.get(decodedKey)).getDestination() + "):");
        destination = scanner.nextLine();
        if (destination.isEmpty())
            destination = courses.get(decode.get(decodedKey)).getDestination();

        System.out.println("Podaj godzine (jako int; lub wartosc ujemna, żeby zostawić poprzednią wartość (" + courses.get(decode.get(decodedKey)).getHour() + "):");
        hour = scanner.nextInt();
        if (hour < 0)
            hour = courses.get(decode.get(decodedKey)).getHour();

        Course course = new Course(destination, departure, hour);
        courses.put(decode.get(decodedKey), course);

        System.out.println("UPDATE at " + decode.get(decodedKey) + " => " + course + "\n\nWcisnij enter");
        System.in.read();
    }

    static void putNewCourse() throws IOException {
        String destination, departure;
        int hour;

        scanner.nextLine();
        System.out.println("Podaj miejsce wyjazdu: ");
        departure = scanner.nextLine();

        System.out.println("Podaj miejsce docelowe: ");
        destination = scanner.nextLine();

        System.out.println("Podaj godzine (jako int): ");
        hour = scanner.nextInt();

        Map<Long, Course> courses = hazelcastInstance.getMap("courses");
        Long key = generateKey();
        Course course = new Course(destination, departure, hour);
        courses.put(key, course);
        System.out.println("PUT " + key + " => " + course + "\n\nWcisnij enter");

        System.in.read();
    }

    static Long generateKey() {
        return (long) Math.abs(new Random().nextLong());
    }
}
