from ds_prod_api.apis.FlaskApi import FlaskApi
from ds_prod_api.abstracts import FeatureExtractor
from pandas import DataFrame as df
from ds_prod_api.abstracts import Model
import os
import pickle
import sys

full_path = os.path.realpath(__file__)
path, filename = os.path.split(full_path)

sys.path.append(path)


class BaselineLmModel(Model):
    def load(self):
        print("Loading baseline model")
        self.estimator = pickle.load(open(path + '/models/lm.p', "rb"), encoding="UTF-8")


    def predict(self, feature_vector):
        print("Received predict in model with feature vector")
        print(feature_vector)
        return str(self.estimator.predict(feature_vector))

    def default(self):
        return "In case of problem return this"


class BaselineFeatureExtractor(FeatureExtractor):
    def get_features(self, json_features_dict):
        print("Getting features for")
        print(json_features_dict)

        return df.from_dict([json_features_dict])


model = BaselineLmModel()
extractor = BaselineFeatureExtractor()

model.load()

print("creating api")
api = FlaskApi(model, extractor)
api.run()
