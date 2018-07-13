FROM continuumio/miniconda3:latest

RUN bash -c "mkdir /opt/ds_prod"
COPY environment.yml /opt/ds_prod/environment.yml
RUN conda env create -f /opt/ds_prod/environment.yml

COPY . /opt/ds_prod/app

WORKDIR /opt/ds_prod/app/
CMD bash -c "source activate {{ cookiecutter.repo_name }} && {{ cookiecutter.python_interpreter }} -m {{ cookiecutter.package_name}}.main"
