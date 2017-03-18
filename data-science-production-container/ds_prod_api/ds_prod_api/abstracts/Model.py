import abc


class Model(abc.ABC):

    def __init__(self):
        pass

    @abc.abstractmethod
    def load(self):
        raise NotImplementedError('users must define load to use this base class')

    @abc.abstractmethod
    def predict(self, feature_vector):
        raise NotImplementedError('users must define predict to use this base class')

    @abc.abstractmethod
    def default(self):
        raise NotImplementedError('users must define predict to use this base class')

