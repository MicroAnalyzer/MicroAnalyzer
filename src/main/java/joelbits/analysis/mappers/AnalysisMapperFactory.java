package joelbits.analysis.mappers;

import org.apache.hadoop.mapreduce.Mapper;

public class AnalysisMapperFactory {
    private AnalysisMapperFactory() {}

    public static Class<? extends Mapper> mapper(String mapper) throws IllegalArgumentException {
        switch (mapper) {
            case "configurations":
                return BenchmarkConfigurationMapper.class;
            default:
                throw new IllegalArgumentException();
        }
    }
}