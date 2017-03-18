import abc


class FeatureExtractor(metaclass=abc.ABCMeta):
    @abc.abstractmethod
    def get_features(self, id):
        raise NotImplementedError('users must define load to use this base class')
