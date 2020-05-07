FROM ubuntu:19.10

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    curl \
    awscli


ARG SHA=12d895ec9f238a94f3e077ae9e2771ed1e583e6c6e743158aa05086487ffdf1c
ARG URL=https://github.com/gohugoio/hugo/releases/download/v0.70.0/hugo_0.70.0_Linux-64bit.deb

RUN curl -fsSL -o /tmp/hugo.deb ${URL} && \
    echo "${SHA} /tmp/hugo.deb" | sha256sum -c - && \
    dpkg -i /tmp/hugo.deb


VOLUME /site

WORKDIR /site

CMD hugo && \
    hugo deploy --maxDeletes 100 && \
    aws cloudfront create-invalidation --distribution-id ECI1PGYSTNAWG --paths "/*"
