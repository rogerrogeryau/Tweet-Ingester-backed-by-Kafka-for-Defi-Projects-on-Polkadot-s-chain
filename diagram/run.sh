docker build -t diagrams .
docker run --rm -it -v "${PWD}":/diagram -w /diagram diagrams python diagram.py