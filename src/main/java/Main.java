import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // Configurare ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Citire din JSON
        List<Angajat> angajati = mapper.readValue(
                new File("src/main/resources/angajati.json"),
                new TypeReference<List<Angajat>>() {}
        );

        // 1. Afișarea listei de angajați folosind referințe la metode
        angajati.forEach(System.out::println);

        // 2. Afișarea angajaților cu salariu peste 2500 RON
        angajati.stream()
                .filter(a -> a.getSalariu() > 2500)
                .forEach(System.out::println);

        // 3. Angajați din aprilie anul trecut cu funcție de conducere
        int anulCurent = LocalDate.now().getYear();
        List<Angajat> conducereAprilie = angajati.stream()
                .filter(a -> a.getDataAngajarii().getYear() == anulCurent - 1 &&
                        a.getDataAngajarii().getMonthValue() == 4 &&
                        (a.getPost().contains("sef") || a.getPost().contains("director")))
                .collect(Collectors.toList());
        conducereAprilie.forEach(System.out::println);

        // 4. Angajați fără funcție de conducere, ordine descrescătoare a salariului
        angajati.stream()
                .filter(a -> !a.getPost().contains("sef") && !a.getPost().contains("director"))
                .sorted((a1, a2) -> Float.compare(a2.getSalariu(), a1.getSalariu()))
                .forEach(System.out::println);

        // 5. Liste nume majuscule
        List<String> numeMajuscule = angajati.stream()
                .map(a -> a.getNume().toUpperCase())
                .collect(Collectors.toList());
        numeMajuscule.forEach(System.out::println);

        // 6. Salarii sub 3000
        angajati.stream()
                .map(Angajat::getSalariu)
                .filter(s -> s < 3000)
                .forEach(System.out::println);

        // 7. Primul angajat
        angajati.stream()
                .min(Comparator.comparing(Angajat::getDataAngajarii))
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("Nu există angajați."));

        // 8. Statistici salarii
        DoubleSummaryStatistics statistici = angajati.stream()
                .collect(Collectors.summarizingDouble(Angajat::getSalariu));
        System.out.println("Salariu mediu: " + statistici.getAverage());
        System.out.println("Salariu minim: " + statistici.getMin());
        System.out.println("Salariu maxim: " + statistici.getMax());

        // 9. Există angajați pe nume Ion?
        angajati.stream()
                .filter(a -> a.getNume().contains("Ion"))
                .findAny()
                .ifPresentOrElse(
                        a -> System.out.println("Firma are cel puțin un Ion angajat."),
                        () -> System.out.println("Firma nu are nici un Ion angajat.")
                );

        // 10. Numărul angajaților din vara anului trecut
        long numarAngajatiVara = angajati.stream()
                .filter(a -> a.getDataAngajarii().getYear() == anulCurent - 1 &&
                        (a.getDataAngajarii().getMonthValue() >= 6 &&
                                a.getDataAngajarii().getMonthValue() <= 8))
                .count();
        System.out.println("Numărul de angajați angajați vara trecută: " + numarAngajatiVara);
    }
}
