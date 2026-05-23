FROM ubuntu:latest
LABEL authors="alzai"

ENTRYPOINT ["top", "-b"]