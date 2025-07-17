#!/usr/bin/env python
# -*- coding: utf-8 -*-

from sys import argv
from pathlib import Path
from html.parser import HTMLParser

REF_TAGS = { ('a', 'href'), ('link', 'href'), ('img', 'src'), ('script', 'src') }

siteroot = Path(argv[1]).resolve()
rootpath = argv[2]
allfiles = set(siteroot.glob('**/*'))
deadlink = False

class RefParser(HTMLParser):

    def __init__(self, root, base):
        HTMLParser.__init__(self)
        self.root = root
        self.base = base

    def check(self, relref, line):
        if relref.startswith('http://') or relref.startswith('https://') or relref.startswith('mailto:'):
            return
        absref = relref
        if absref.endswith('/'):
            absref += 'index.html'
        if absref.startswith(rootpath):
            absref = (self.root / absref[len(rootpath):]).resolve()
        else:
            absref = (self.base.parent / absref).resolve()
        if absref not in allfiles:
            global deadlink
            deadlink = True
            print('[%s:%s] dead link %s' % (self.base, line, relref))
 
    def handle_starttag(self, tag, attrs):
        for key, value in attrs:
            if (tag, key) in REF_TAGS:
                link = value.split('#')[0]
                self.check(link, self.getpos()[0])

for f in allfiles:
    if f.name.endswith('.html'):
        with open(f, 'rt', encoding='utf-8') as h:
            p = RefParser(siteroot, f)
            p.feed(h.read())

if deadlink:
    exit(255)
