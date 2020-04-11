FROM ubuntu:19.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    curl \
    awscli


ARG SHA=055d33cd3ca505716761d568b83a1adb11310618c8ae16f4caa20f5d6add18d6
ARG URL=https://github.com/gohugoio/hugo/releases/download/v0.69.0/hugo_0.69.0_Linux-64bit.deb

RUN curl -fsSL -o /tmp/hugo.deb ${URL} && \
    echo "${SHA} /tmp/hugo.deb" | sha256sum -c - && \
    dpkg -i /tmp/hugo.deb


VOLUME /site

WORKDIR /site

CMD hugo && \
    hugo deploy --maxDeletes 100 && \
    aws cloudfront create-invalidation --distribution-id ECI1PGYSTNAWG --paths "/*"
