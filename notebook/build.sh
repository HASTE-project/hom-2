#!/bin/sh

docker build --platform linux/amd64 --tag benblamey/hom-impl-2.notebook:latest .
docker login
docker push benblamey/hom-impl-2.notebook:latest