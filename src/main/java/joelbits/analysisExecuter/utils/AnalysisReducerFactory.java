package joelbits.analysisExecuter.utils;

import joelbits.analysisExecuter.reducers.BenchmarkConfigurationReducer;
import org.apache.hadoop.mapreduce.Reducer;

public final class AnalysisReducerFactory {
    private AnalysisReducerFactory() {}

    public static Class<? extends Reducer> reducer(String reducer) throws IllegalArgumentException {
        switch (reducer) {
            case "configurations":
                return BenchmarkConfigurationReducer.class;
            default:
                throw new IllegalArgumentException();
        }
    }
}
