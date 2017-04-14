FROM continuumio/miniconda3:latest

#RUN apt-get -y update && apt-get install -y git python-pip
RUN apt-get install -y git python-pip

RUN bash -c "mkdir /ds_prod"
ADD environment.yml /ds_prod/environment.yml
RUN conda env create -f /ds_prod/environment.yml python=2.7

ADD . /ds_prod/app

WORKDIR /ds_prod/app/
CMD bash -c "source activate ds_prod && python main.py"
