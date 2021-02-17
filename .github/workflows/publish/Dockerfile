FROM ubuntu:20.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y curl

ARG SHA=b4deef3011141968dd55ea28d67221e26d0a7986f4d7db8291abe2179ec36f9d
ARG URL=https://github.com/gohugoio/hugo/releases/download/v0.80.0/hugo_0.80.0_Linux-64bit.deb

RUN curl -fsSL -o /tmp/hugo.deb ${URL} && \
    echo "${SHA} /tmp/hugo.deb" | sha256sum -c - && \
    dpkg -i /tmp/hugo.deb


VOLUME /site

WORKDIR /site

CMD hugo && \
    hugo deploy --maxDeletes 100 --invalidateCDN
