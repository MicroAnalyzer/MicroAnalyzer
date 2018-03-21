# MicroAnalyzer

This project contains a set of modules dealing with different responsibilities of the mining software repositories process.
The MicroAnalyzer framework has a plugin architecture allowing users to extend the framework to support a new mining tasks.
As a result researchers and practitioners are not required to create new specific mining tools from scratch for their projects.
The hotspots of the MicroAnalyzer architecture allow for plugin types such as language parsers, repository source cloners, dataset analyzers, and CVS
data extractors.

## How To Compile Sources

If you checked out the project from GitHub you can build the project with maven using:

```
mvn clean install
```

## Modules
The framework contains three modules each focusing on a specific part of the mining process. A user of the framework can run
any of the modules separately since they are not dependent on each other, e.g., the cloning module could be used to clone repositories
from a hosting service such as GitHub, or the analysis module could be used to perform analyses on data stored in Hadoop flat files (MapFile
and SequenceFile).

**Cloning**: 

This module is used for cloning remote software repositories into a local directory named _/repositories_. This directory is
created by the framework if it does not exist. Begin the cloning process by running the framework with the following two 
parameters: *-fileName -repositorySource*

Example: 
```
java -cp micro-analyzer.jar joelbits.modules.cloning.CloningModule repositories.json github
```

Explanation of parameters:

* *-fileName* is the input file containing names of the repositories to be cloned. The file must contain the full 
names as required by each repository source, e.g., apache/logging-log4j2 for a GitHub repository.
* *-repositorySource* The source, e.g., github, of the remote repositories. Must be stated so that the framework uses the
correct cloning plugin.

**Preprocessing**:

This module pre-processes the projects found in the _/repositories_ folder which must exist in the same directory as the 
framework jar is run. There are 5 possible input parameters; *-connector -language -fileName -source -datasetName*

Example: 
```
java -cp micro-analyzer.jar joelbits.modules.preprocessing.PreProcessorModule git java metadata.json github jmh_dataset
```

Explanation of parameters:

* *-connector* informs which connector should be used to connect to the repositories cvs. The reason for 
using a connector is to be able to collect the history of the repository development. 
* *-language* represent which language parser should be used to extract the raw data. 
* *-fileName* is the name of the input file that contains the projects' metadata. 
* *-source* identifies the source of the repositories, i.e., where the metadata file were retrieved from, e.g., github. 
* *-datasetName* is optional and will be the name given to the created dataset. If this parameter is left out, a default
name will be given to the created dataset.


**Analysis**:

This module is used to analyze datasets created by MicroAnalyzer. A dataset consists of two files; a Hadoop MapFile containing
data on a project level, and a Hadoop SequenceFile containing data on a source code level. The output of the analysis module is
a text file containing the analysis result. An analysis process is initiated using a minimum of three input parameters;  
*-analysisPlugin -analysis -outputFileName -dataset(s)*

Example: 
```
java -cp micro-analyzer.jar joelbits.modules.analysis.AnalysisModule jmh benchmarkConfigurations configurations.txt jmh_dataset
```

Explanation of parameters:

* *-analysisPlugin* identifies which analysis plugin to use. Since an analysis plugin may contain multiple analyses the user 
should also add a parameter identifying which specific analysis to run.
* *-analysis* is the specific analysis to run, corresponding to the mapper and reducer implementation parts of the analysis plugin.
* *-outputFileName* becomes the name for the created output text file containing the analysis results.
* *-dataset(s)* is an optional parameter and if used, it names which specific dataset(s) should be subject for analysis. If 
this parameter is left out the default dataset name will be used (which is the default name for the created dataset after 
preprocessing).