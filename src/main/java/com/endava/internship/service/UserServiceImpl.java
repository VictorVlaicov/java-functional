package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        Comparator<User> comparator = Comparator.comparing(User::getAge,Comparator.reverseOrder())
                                                .thenComparing(User::getFirstName);
        return users.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return  users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        Predicate<User> predicate = user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE);
        return users.stream()
                .filter(predicate)
                .findAny();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .map(user -> Pair.of(user.getPrivileges().size(),user))
                .collect(Collectors.groupingBy(Pair::getKey,Collectors.mapping(Pair::getValue,Collectors.toList())));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream()
                .mapToDouble(User::getAge)
                .average()
                .orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()))
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(x -> x.size() == 1)
                .map(List::stream)
                .flatMap(Stream::findAny);

    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates).reduce(p->true,Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                .map(privilege -> Pair.of(privilege,user)))
                .collect(Collectors.groupingBy(Pair::getKey,Collectors.mapping(Pair::getValue,Collectors.toList())));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName,Collectors.counting()));
    }
}
