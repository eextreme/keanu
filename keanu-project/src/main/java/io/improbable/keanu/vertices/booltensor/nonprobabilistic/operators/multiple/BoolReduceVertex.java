package io.improbable.keanu.vertices.booltensor.nonprobabilistic.operators.multiple;

import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.booltensor.nonprobabilistic.NonProbabilisticBool;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BoolReduceVertex extends NonProbabilisticBool {
    private final List<? extends Vertex<BooleanTensor>> inputs;
    private final BiFunction<BooleanTensor, BooleanTensor, BooleanTensor> reduceFunction;

    public BoolReduceVertex(Collection<Vertex<BooleanTensor>> input,
                            BiFunction<BooleanTensor, BooleanTensor, BooleanTensor> reduceFunction) {
        if (input.size() < 2) {
            throw new IllegalArgumentException("BoolReduceVertex should have at least two input vertices, called with " + input.size());
        }

        this.inputs = new ArrayList<>(input);
        this.reduceFunction = reduceFunction;
        setParents(inputs);
    }

    public BoolReduceVertex(BiFunction<BooleanTensor, BooleanTensor, BooleanTensor> f, Vertex<BooleanTensor>... input) {
        this(Arrays.asList(input), f);
    }

    @Override
    public BooleanTensor sample(KeanuRandom random) {
        return applyReduce((vertex) -> vertex.sample(random));
    }

    @Override
    public BooleanTensor getDerivedValue() {
        return applyReduce(Vertex::getValue);
    }

    private BooleanTensor applyReduce(Function<Vertex<BooleanTensor>, BooleanTensor> mapper) {
        Iterator<? extends Vertex<BooleanTensor>> vertices = inputs.iterator();

        BooleanTensor c = mapper.apply(vertices.next());
        while (vertices.hasNext()) {
            c = reduceFunction.apply(c, mapper.apply(vertices.next()));
        }
        return c;
    }
}
