
# Building a Large-Language-Model (LLM) from scratch



### Author: Manoj Ankireddy
<!-- ### UIN :  -->
### Email: ankireddymanoj162@gmail.com

##  Description

This project focussed on implementing an LLM encoder using parallel distributed computations in the cloud, using Hadoop MapReduce functionality and deploying the application on AWS Elastic MapReduce (EMR).



##  Project structure

1. **This project consists of 2 parts**:

   a. First is a simple word counter Map reduce program.

   b. Second is a MapReduce implementation of LLM encoder, this will be trained over a huge corpus of input data and will generate vector embeddings of words.

2. Dataset used for training the model [Wikipedia Text](https://huggingface.co/datasets/Daniel-Saeedi/wikipedia/blob/main/wikipedia-10.txt).

3. For the first part we are

4. For the encoder part, I'm dividing the data into 256MB chuncks, where each shard is handle bt a mapper and pass over that to a reducer.

   a. For each Mapper we are splitting the data into each sentence and encoding the sentence using Jtokkit to generate Byte-Pair encodings of words.

   b. We are again creating features and output labels using the tockenized sentence by sliding each word, we are mapping the current word as a prediction to previous sentence.

   c. Eg: "We are from UIC" => converting into tokens => [123, 23, 45, 12, 49, 11] => features = [123, 23, 45, 12, 49], labels = [23, 45, 12, 49, 11]

   ![image](https://github.com/user-attachments/assets/09976454-5f4a-4769-8721-1fa9cb14ec2d)

   d.The model is getting trained using this feastures and labels data.

   e. Once the model is trained for each shard, we are mapping the input words to the respective vectors that are getting from the model.

   f. I'm seria;izing the vector embedding which is in the form is **INDArray** to **TextWritable**.

5. The Reducer receiced the sorted data as the input and will do the processing.

   a. The Reducer first deserializing the input I.e. the IteratorOf[TextWtitable] it received into Itetator[Array]

   b. After this conversion we are averaging all the embeddings of a word to one, this will reduce the Noice and can have better Contextual Understanding.



** On a High level this is the view of this second application **

<img width="1486" alt="image" src="https://github.com/user-attachments/assets/cb6cc9f3-55e1-4ffd-abfd-1503cfccacc5">





### Environment
OS: Mac

### IDE: IntelliJ IDEA 2022.2.3 (Ultimate Edition)

### SCALA Version: 2.13.12

[//]: # (SBT Version: 1.8.3)

### Hadoop Version: 3.3.4

Running the test file
Test Files can be found under the directory src/test

````
sbt clean compile test
````
## Running the project in local.

1) Clone this repository
```
git clone git@github.com:nithish-kumar-t/building-a-Large-Language-Model-LLM-from-scratch.git
```


2) cd to the Project
```
cd building-a-Large-Language-Model-LLM-from-scratch
```
3) update the jars
```
sbt clean update
```

4) Create fat jat using assembly
```
sbt assembly
# This will create a fat Jar
```

5) we can then run UT's and FT's using below
```
sbt test
```

6) SBT application can contain multiple mains, this project has 2, so to check the correct main
```
➜building-a-Large-Language-Model-LLM-from-scratch git:(feature) ✗ sbt
[info] started sbt server
sbt:LLM-hw1-jar>
sbt:LLM-hw1-jar> show discoveredMainClasses
* com.trainingLLM.LLMEncoder
* com.trainingLLM.TokenizationJob
```

7) Create fat jat using assembly
```
hadoop jar <JarName> <Method to run> <environment>
eg: hadoop jar target/scala-2.13/llm-hw1.jar com.trainingLLM.TokenizationJob env=local
```

## Running the Project in AWS

1) Create a Library structure like below in S3

 <img width="244" alt="image" src="https://github.com/user-attachments/assets/187d514d-0309-47f1-91ca-e031e87b1936">


2) Start a new cluster in AWS EMR, use default configuration,


3) After a cluster is created open the cluster and we can add our MR job as steps. It will show like below, select the Jar from your s3 and give env values.

<img width="1417" alt="image" src="https://github.com/user-attachments/assets/80e4cd8b-6f29-4b05-b15f-34ee6d7b47c8">

4) We can also able to chain the steps, once you add step it will start running, based on the order


<img width="1410" alt="image" src="https://github.com/user-attachments/assets/ab0f4514-6562-4abe-a6c6-41ab0fb7dc48">


5) Once the Job completes, output will be available under s3, as per pre-configuration in step 1

<img width="313" alt="image" src="https://github.com/user-attachments/assets/3450778b-ea14-46e6-8a42-21e89b8bb4fc">




## Prerequisites

1. **Hadoop**: Set up Hadoop on your local machine or cluster.

2. **AWS Account**: Create an AWS account and familiarize yourself with AWS EMR.

3. **Deeplearning4j Library**: Ensure that you have the Java Deeplearning4j Library integrated into your project for getting the model.

4. **Scala, Java and Hadoop**: Make sure Scala, Java and Hadoop (Scala (above 2), Java 11, hadoop 3.3.6)  are installed and configured correctly.

5. **Git and GitHub**: Use Git for version control and host your project repository on GitHub.

6. **IDE**: Use an Integrated Development Environment (IDE) for coding and development.


## Usage

Follow these steps to execute the project:

1. **Data Gathering**: Ensure you have selected a dataset and it will be ready to get processed by you MR jobs.

2. **Configuration**: Set up the necessary configuration parameters and input file paths.

3. **MapReduce Execution**:

   a. Run the TockenizeMapReduce Job to generate tokens for each word and it's count.

   b. Run the LLMEncoder job to create vector embeddings, it will create vector embedings and write that in a file.

4. **Results**: Examine the results obtained from the MapReduce jobs.

   a. Word counter should output the word, it's Unique token and the count of the word.

   b. LLM Encoder should output the vector embeddings, word and it's embedding.

5. **Deployment on AWS EMR**: If required, deploy the project on AWS EMR to train more data.


## Unit / Regression Testing

**Code coverage report**


<img width="597" alt="Screenshot 2024-10-05 at 9 47 50 PM" src="https://github.com/user-attachments/assets/08e596ea-2c3a-410a-bb25-b3e387d95027">




