
# Data Science Production Container tutorial
This tutorial will help you get started on using the data science production container. Firstly an explanation of the framework and it's components will be done. Secondly a hands-on exercise to get a predictive model into a running docker container.

## The framework
The framework helps you to create a simple HTTP API which can expose predictive models. It has only one endpoint which is `/predict`. The framework is built on top of Flask. It has a number of abstract classes which need to be implemented and passed to an API object:
 
1. Abstract class Model: This abstract class is used to handle the predictive model. The idea is load your model from some source (e.g. in the example below we'll load a pickled file from disk). And use the model to do predictions. Training the model is thus not in scope of this framework and should be done before implementation.
2. Abstract class FeatureExtractor: This abstract class is used to transform the HTTP request body to a feature vector which is usable for the model. An implementation could be just passing the body as a whole, or maybe a database lookup for some additional features etc.
3. The API class, this class is the real Flask wrapper and is constructed using a Model and FeatureExtractor implemented class. On `POST` call to the `/predict` endpoint the message body will be parsed to a dict, sent to the feature extractor, and the features will then be passed to the model to do the real prediction. This prediction is finally returned to the client.


## Exercise

This exercise will help you get started using the framework. The goal is to bring a house price prediction model into production.
Building the model is beyond the scope of this exercise. Therefore a simple regression model was created based a Kaggle dataset [Kaggle dataset](https://www.kaggle.com/c/house-prices-advanced-regression-techniques). It is created with the scikit-learn package and can be found in the `Exercise\models` direcotry as a pickle file. 

This exercise will be based of the Exercise directory, a number of files have been prepared, during the exercise all steps will be explained. The exercise contains the same implementation as the DemoContainer, whenever you get stuck, you can peak in that directory for a little inspiration ;-).

#### Step 1
The `main.py` file will be used for the implementation of the framework. In order to implement it, we first need to import the framework definitions. 
At the top of the file create the import statements.


```
from ds_prod_api.apis.FlaskApi import FlaskApi
from ds_prod_api.abstracts import FeatureExtractor
from ds_prod_api.abstracts import Model
```

#### Step 2
Before creating the API we need to implement a Model class and a FeatureExtractor, first start with the Model.
In the `main.py` file add a new class definition which implements `Model`. Also implement 3 abstract functions:

1. load(self): This function is used to initialize the model. In our implementation we need to depickle the model from the models directory.
2. predict(self, features) -> return prediction: This function is used on incoming API calls. Implement it such that the feature_vector is passed to the predict function of the depickled model, and return the response.
3. default(self) -> return default_value: This function is used when there is an error during handling the API call, and then return a default value (e.g. average house price).
 

#### Step 3
Implement the FeatureExtractor abstract class. Als implement one abstract function:

1. get_features(self, request_body_dict)-> return features: This function is called on every api call to create a feature vector. The request body is passed to this function. Implement it such that a pandas Dataframe is create from the request body. (hint: `from pandas import DataFrame as df` and use the `df.from_dict()` function)

#### Step 4
Now we have implemented all necessary components and can create, and run the api. Create an object for the implemented model and feature extractor and pass these to the constructor of the api.

```
model = BaselineLmModel()
extractor = BaselineFeatureExtractor()

model.load()

api = FlaskApi(model, extractor)
api.run()
```

#### Step 5
Add the dependencies of your project to the `environment.yaml` file. This file will be used when building the docker image to create a virtual environment. It will look something like this:

```
name: ds_prod
dependencies:
- flask=0.12.2
- python=3
- numpy=1.13.1
- scikit-learn=0.18.1
- dill=0.2.6
- pandas=0.20.3
- scipy=0.19.1

- pip:
  - git+https://github.com/BigDataRepublic/bdr-engineering-stack.git@develop#subdirectory=data-science-production-container/ds_prod_api
```

#### Step 6
We have now implemented all components and can build the docker image. In the commandline run:

```
docker build . -t MyFirstDSProdContainer
```

During the docker build a number of steps will be taken. The base image for this container is the Miniconda docker image, this has conda preinstalled. Then all the files from the directory will be added to the container. A conda env will be created based on the `environment.yaml` file. 


#### Step 7 
Run the docker image, and expose port 5000.

```
docker run -p5000:5000 MyFirstDSProdContainer
```


#### Step 8
Send a POST call to the api, and check the reponse. The model takes two features: `LotArea` and `YearBuilt`.


```
curl \
    -H "Content-Type: application/json" \
    -X POST \
    -d '[{"LotArea": 200,"YearBuilt":1978}]' \
    http://localhost:5000/predict
```

You can also create your own data json, if you put a features.json file with `[{"LotArea": 200,"YearBuilt":1978}]` in the same directory, run:

```
curl \
    -H "Content-Type: application/json" \
    -X POST \
    -d @features.json \
    http://localhost:5000/predict

Alternatively, you can use a GUI like [postman](https://www.getpostman.com/).

