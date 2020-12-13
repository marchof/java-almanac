FROM ubuntu:20.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    curl \
    awscli


ARG SHA=594ca47eec1c4e18f93c2e49a41aad53b472895ce93b7c6b79e72f472283ce17
ARG URL=https://github.com/gohugoio/hugo/releases/download/v0.74.3/hugo_0.74.3_Linux-64bit.deb

RUN curl -fsSL -o /tmp/hugo.deb ${URL} && \
    echo "${SHA} /tmp/hugo.deb" | sha256sum -c - && \
    dpkg -i /tmp/hugo.deb


VOLUME /site

WORKDIR /site

CMD hugo && \
    hugo deploy --maxDeletes 100 && \
    aws cloudfront create-invalidation --distribution-id ECI1PGYSTNAWG --paths "/*"
