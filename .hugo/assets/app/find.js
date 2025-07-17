"use strict";

let searchfield = document.getElementById("searchfield");
let resultinfodiv = document.getElementById("resultinfo");
let resultfiltersdiv = document.getElementById("resultfilters");
let resultsdiv = document.getElementById("results");
let findmorediv = document.getElementById("findmore");

let debounceTimerId;
let lastSearch;
let searchParams = new URLSearchParams() 

function setQueryWithDelay(q) {
	clearTimeout(debounceTimerId);
	debounceTimerId = setTimeout(() => {
		searchParams.set("q", q);	
	    searchParams.delete("s");
		executeQuery(true, false)
	}, 200);
}

function executeQuery(updatehistory, append) {
	const searchstr = searchParams.toString();
	if (lastSearch == searchstr) {
		return;
	}
	lastSearch = searchstr;

	if (updatehistory) {
		const url = new URL(window.location);
		url.search = searchstr;
		window.history.pushState({}, '', url);
	}

	resultinfodiv.innerHTML = "Finding..."
	fetch('{{ $.Site.Params.Api.Find }}?' + searchstr)
		.then(response => response.json())
		.then(result => {
			if (lastSearch == searchstr) {
				renderResult(result, append);
			}
		});
}

function addFilter(group, value) {
	searchParams.set(group, value);
	searchParams.delete("s");
	executeQuery(true, false);
}

function removeFilter(group) {
	searchParams.delete(group);
	searchParams.delete("s");
	executeQuery(true, false);
}

function page(start) {
	searchParams.set("s", start);
	executeQuery(false, true);
}

function tag(name, ...children) {
	const tag = document.createElement(name);
	children.forEach(c => tag.append(c));
	return tag;
}

function renderResult(r, append) {
	resultinfodiv.innerHTML = "";
	if (r.more) {
		resultinfodiv.append("more than ");
	}
	resultinfodiv.append(r.hitCount.toLocaleString() + " hits");
	resultfiltersdiv.innerHTML = "";
	for (const t of r.taginfos) {
		const c = tag("span", t.count.toLocaleString() + "x");
		c.className = "count";
		const s = tag("span", c, t.value);
		s.className = "tag";
		if (t.filtered) {
			s.classList.add("filtered");
			s.onclick = () => removeFilter(t.group);
		} else {
			s.onclick = () => addFilter(t.group, t.value);
		}
		s.setAttribute("title", t.group);
		resultfiltersdiv.append(s, " ");
	}
	
	if (!append) {
	    resultsdiv.innerHTML = "";
	}
	for (const h of r.hits) {
		const p = tag("p");
		
		const a = tag("a", h.title);
		a.setAttribute("href", h.url);
		p.append(a);
		
		for (const key in h.tags) {
			const s = tag("span", h.tags[key]);
			s.className = "tag";
			s.setAttribute("title", key);
			s.onclick = () => addFilter(key, h.tags[key]);
			p.append(" ", s);
		}
		
		p.append(tag("br"));
		
		if (h.more.length) {
			const s = tag("span", "→ Also in version ");
			s.className = "more";
			let separator = false;
			for (const m of h.more) {
				if (separator) {
					s.append(", ");
				}
				const a = tag("a", m.version);
				a.setAttribute("href", m.url);
				s.append(a);
				separator = true;
			}
			p.append(s, tag("br"));
		}
		
		const preview = tag('span');
		preview.innerHTML = h.preview;
		p.append(preview);
		
		resultsdiv.append(p);
	}
	
	findmorediv.innerHTML = "";
	if (r.more) {
		const span = tag("span", `load more than ${r.hitCount} hits`);
		span.onclick = e => page(r.hitCount);
		findmorediv.append(span);
	}
	
}

function getParamsFromURL() {
	searchParams = new URLSearchParams(window.location.search);
	searchfield.value = searchParams.get('q');
	executeQuery(false, false);
}

window.addEventListener("load", (event) => getParamsFromURL());
window.addEventListener("popstate", (event) => getParamsFromURL());

searchfield.addEventListener("keyup", (event) => setQueryWithDelay(searchfield.value));
