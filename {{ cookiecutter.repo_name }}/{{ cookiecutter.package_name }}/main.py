# -*- coding: utf-8 -*-
import click
from ds_prod_api.apis.FlaskApi import FlaskApi
from ds_prod_api.abstracts import FeatureExtractor
from pandas import DataFrame as df
from ds_prod_api.abstracts import Model
import pickle
from pathlib import Path
from dotenv import find_dotenv, load_dotenv
import os
import logging


class BaselineLmModel(Model):
    def load(self):
        print("Loading baseline model")
        self.estimator = pickle.load(open(os.path.join(project_dir, 'models', 'lm.p'), "rb"), encoding="UTF-8")


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

        return df.from_dict(json_features_dict)


@click.command()
def main():
    logger = logging.getLogger(__name__)
    model = BaselineLmModel()
    extractor = BaselineFeatureExtractor()

    model.load()

    logger.info("creating api")
    api = FlaskApi(model, extractor)
    api.run()


if __name__ == '__main__':
    log_fmt = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    logging.basicConfig(level=logging.INFO, format=log_fmt)

    # not used in this stub but often useful for finding various files
    project_dir = Path(__file__).resolve().parents[2]

    # find .env automagically by walking up directories until it's found, then
    # load up the .env entries as environment variables
    load_dotenv(find_dotenv())

    main()
