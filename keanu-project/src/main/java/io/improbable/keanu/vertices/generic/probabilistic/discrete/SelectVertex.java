package io.improbable.keanu.vertices.generic.probabilistic.discrete;

import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.generic.probabilistic.Probabilistic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class SelectVertex<T> extends Probabilistic<T> {

    private final Map<T, DoubleVertex> selectableValues;
    private final Random random;

    public SelectVertex(Map<T, DoubleVertex> selectableValues, Random random) {
        this.selectableValues = selectableValues;
        this.random = random;
        setParents(selectableValues.values());
    }

    public SelectVertex(Map<T, DoubleVertex> selectableValues) {
        this(selectableValues, new Random());
    }

    public Map<T, DoubleVertex> getSelectableValues() {
        return selectableValues;
    }

    @Override
    public T sample() {
        double sumP = sumProbabilities();
        double p = random.nextDouble();
        double sum = 0;

        if (sumP == 0.0) {
            throw new IllegalArgumentException("Cannot sample from a zero probability setup.");
        }

        T value = null;
        for (Map.Entry<T, DoubleVertex> entry : selectableValues.entrySet()) {
            sum += entry.getValue().getValue() / sumP;
            if (p < sum) {
                value = entry.getKey();
                break;
            }
        }
        if (value == null) {
            T[] values = (T[]) selectableValues.keySet().toArray();
            value = values[values.length - 1];
        }

        return value;
    }

    @Override
    public double density(T value) {
        double sumP = sumProbabilities();
        if (sumP == 0.0) {
            throw new IllegalArgumentException("Cannot sample from a zero probability setup.");
        }
        return selectableValues.get(value).getValue() / sumP;
    }

    @Override
    public Map<String, Double> dDensityAtValue() {
        throw new UnsupportedOperationException();
    }

    private double sumProbabilities() {
        double sumP = 0.0;
        for (DoubleVertex p : selectableValues.values()) {
            sumP += p.getValue();
        }
        return sumP;
    }
}
