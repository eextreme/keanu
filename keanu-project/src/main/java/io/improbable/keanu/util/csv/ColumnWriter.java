package io.improbable.keanu.util.csv;

import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.vertices.Vertex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.improbable.keanu.util.csv.WriteCsv.findLongestTensor;

public class ColumnWriter extends Writer {

    private static String DEFAULT_EMPTY_VALUE = "-";
    private static String HEADER_STYLE = "{%d}";

    private List<? extends Vertex<? extends Tensor>> vertices;
    private String emptyValue;

    public ColumnWriter(List<? extends Vertex<? extends Tensor>> vertices, String emptyValue) {
        this.vertices = vertices;
        this.emptyValue = emptyValue;
    }

    public ColumnWriter(List<? extends Vertex<? extends Tensor>> vertices) {
        this(vertices, DEFAULT_EMPTY_VALUE);
    }

    @Override
    File toFile(String file) {
        List<String[]> data = new ArrayList<>();
        int longestTensor = findLongestTensor(vertices);

        for (int i = 0; i < longestTensor; i++) {
            List<String> row = new ArrayList<>();
            for (Vertex<? extends Tensor> vertex : vertices) {
                List<Object> flatList = vertex.getValue().asFlatList();
                if (i < flatList.size()) {
                    row.add(flatList.get(i).toString());
                } else {
                    row.add(emptyValue);
                }

            }
            String[] rowToString = new String[row.size()];
            data.add(row.toArray(rowToString));
        }
        return writeToFile(file, data);
    }

    @Override
    Writer withDefaultHeader() {
        String[] header = new String[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            header[i] = String.format(HEADER_STYLE, vertices.get(i).getId());
        }
        withHeader(header);
        withHeaderEnabled(true);
        return this;
    }

    public Writer withEmptyValue(String emptyValue) {
        this.emptyValue = emptyValue;
        return this;
    }
}