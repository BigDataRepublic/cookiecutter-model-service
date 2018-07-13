from setuptools import find_packages, setup

setup(
    name='{{ cookiecutter.repo_name }}',
    packages=find_packages(),
    version='0.0.1',
    description='{{ cookiecutter.description }}',
    author='{{ cookiecutter.author_name }}',
    author_email='{{ cookiecutter.author_email }}',
    license='{% if cookiecutter.open_source_license == 'MIT' %}MIT{% elif cookiecutter.open_source_license == 'BSD-3-Clause' %}BSD-3{% elif cookiecutter.open_source_license == 'Apache-2.0' %}Apache License 2.0{% endif %}',
    long_description="README.md",
{% if cookiecutter.python_interpreter == 'python3' %}
    python_requires='>3.5',
{% endif %}
    install_requires=[
                     "click",
                     "python-dotenv>=0.5.1",
                     "flask==0.12.2",
                     "numpy==1.13.1",
                     "scikit-learn==0.18.1",
                     "dill==0.2.6",
                     "pandas==0.20.3",
                     "scipy==0.19.1",
                     "ds_prod_api",
    ],
    dependency_links=[
                      "https://github.com/BigDataRepublic/ds-prod-api/tarball/master#egg=ds_prod_api-0.0.1",
    ],
    include_package_data=True,
    package_data={
                  '{{ cookiecutter.package_name }}': ['models/*.*'],
                 },
    setup_requires=["pytest-runner"],
    tests_require=["pytest"]
)
