# Data Science Production Container

#### Getting started


To run the baseline model, we first need to download the pickled version of
this model.

```
# pwd
${LOCAL_PATH}/bdr-engineering-stack/data-science-production-container/DemoContainer
# mkdir models
# wget -O models/PartyClassifier.pkl http://bdr-engineering-models.s3-eu-central-1.amazonaws.com/PartyClassifier.pkl
```

Now we can build the container that will contain the service.

```
# docker build -t bdr/verkiezingen:1.0 .
# docker run -p 5000:5000 bdr/verkiezingen:1.0
```

Now you should be able to post requests to the REST API on port 5000. To
achieve this you can for example use the commandline tool curl:

```
curl \
    -H "Content-Type: application/json" \
    -X POST \
    -d '{"my_feature_1": "xyz","my_feature_2":"xyz"}' \
    http://localhost:5000/predict
```

Alternatively, you can use a GUI like [postman](https://www.getpostman.com/).