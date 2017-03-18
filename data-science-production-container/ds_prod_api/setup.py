from setuptools import setup

setup(name='ds_prod_api',

packages=['ds_prod_api', 'ds_prod_api.abstracts', 'ds_prod_api.apis'],
include_package_data=True,
version='0.1',
install_requires=[
  "flask"
],
entry_points={
    'console_scripts': [
    'ds_prod_api = ds_prod_api.command_line:main']
})