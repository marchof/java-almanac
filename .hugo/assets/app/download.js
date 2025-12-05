"use strict";

Vue.component('downloadlist', {
    template: `
        <div>
        <div style="overflow-x:auto;">
        <table style="width:100%;white-space:nowrap;">
          <thead>
            <tr>
              <th>Product</th>
              <th>Version</th>
              <th>Type</th>
              <th>Platform</th>
              <th style="width:400px">Download Link</th>
            </tr>
            <tr>
              <td><select style="width:100%" v-model="product"><option v-for="o in productset" v-bind:value="o">{{ o.name }}</option></select></td>
              <td><select style="width:100%" v-model="version"><option v-for="o in versionset" v-bind:value="o">{{ o.name }}</option></select></td>
              <td><select style="width:100%" v-model="type"><option v-for="o in typeset" v-bind:value="o">{{ o.name }}</option></select></td>
              <td><select style="width:100%" v-model="platform"><option v-for="o in platformset" v-bind:value="o">{{ o.name }}</option></select></td>
              <td></td>
            </tr>
          </thead>
          <tbody>
            <tr v-if="message">
              <td colspan="5"><i>{{ message }}</i></td>
            </tr>
            <tr v-for="package in packages">
              <td>{{ package.distribution }}</td>
              <td>{{ package.java_version }}</td>
              <td>{{ package.package_type }}</td>
              <td>{{ package.operating_system }}-{{ package.architecture }}</td>
              <td style="max-width:400px;overflow:hidden;text-overflow:ellipsis;"><a style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" v-bind:href="package.links.pkg_download_redirect">{{ package.filename }}</a></td>
            </tr>
          </tbody>
        </table>
        </div>
        </div>
    `,
    data() {
        return {
            productset: [],
            versionset: [
                { id: 'all', name:'All', query: {} },
                { id: '6',  name: '6',   query: { jdk_version: '6' } },
                { id: '7',  name: '7',   query: { jdk_version: '7' } },
                { id: '8',  name: '8',   query: { jdk_version: '8' } },
                { id: '9',  name: '9',   query: { jdk_version: '9' } },
                { id: '10', name: '10',  query: { jdk_version: '10' } },
                { id: '11', name: '11',  query: { jdk_version: '11' } },
                { id: '12', name: '12',  query: { jdk_version: '12' } },
                { id: '13', name: '13',  query: { jdk_version: '13' } },
                { id: '14', name: '14',  query: { jdk_version: '14' } },
                { id: '15', name: '15',  query: { jdk_version: '15' } },
                { id: '16', name: '16',  query: { jdk_version: '16' } },
                { id: '17', name: '17',  query: { jdk_version: '17' } },
                { id: '18', name: '18',  query: { jdk_version: '18' } },
                { id: '19', name: '19',  query: { jdk_version: '19' } },
                { id: '20', name: '20',  query: { jdk_version: '20' } },
                { id: '21', name: '21',  query: { jdk_version: '21' } },
                { id: '22', name: '22',  query: { jdk_version: '22' } },
                { id: '23', name: '23',  query: { jdk_version: '23' } },
                { id: '24', name: '24',  query: { jdk_version: '24' } },
                { id: '25', name: '25',  query: { jdk_version: '25' } },
                { id: '26', name: '26',  query: { jdk_version: '26', release_status: 'ea' } },
				{ id: '26', name: '27',  query: { jdk_version: '27', release_status: 'ea' } }
            ],
            typeset: [
                { id: 'all', name: 'All', query: {} },
                { id: 'jdk', name: 'JDK', query: { package_type: 'jdk' } },
                { id: 'jre', name: 'JRE', query: { package_type: 'jre' } }
            ],
            platformset: [
                { id: 'all',             name: 'All',             query: { } },
                { id: 'aix-ppc64',       name: 'aix-ppc64',       query: { operating_system: 'aix',     architecture: 'ppc64' } },
                { id: 'alpine-arm32',    name: 'alpine-arm32',    query: { operating_system: 'linux',   architecture: 'arm', libc_type: 'musl' } },
                { id: 'alpine-arm64',    name: 'alpine-arm64',    query: { operating_system: 'linux',   architecture: 'arm64,aarch64', libc_type: 'musl' } },
                { id: 'alpine-x64',      name: 'alpine-x64',      query: { operating_system: 'linux',   architecture: 'x64,amd64', libc_type: 'musl' } },
                { id: 'linux-arm32',     name: 'linux-arm32',     query: { operating_system: 'linux',   architecture: 'arm' } },
                { id: 'linux-arm64',     name: 'linux-arm64',     query: { operating_system: 'linux',   architecture: 'arm64,aarch64', libc_type: 'libc,glibc' } },
                { id: 'linux-ia64',      name: 'linux-ia64',      query: { operating_system: 'linux',   architecture: 'ia64' } },
                { id: 'linux-ppc64',     name: 'linux-ppc64',     query: { operating_system: 'linux',   architecture: 'ppc64' } },
                { id: 'linux-ppc64le',   name: 'linux-ppc64le',   query: { operating_system: 'linux',   architecture: 'ppc64le' } },
                { id: 'linux-riscv64',   name: 'linux-riscv64',   query: { operating_system: 'linux',   architecture: 'riscv64' } },
                { id: 'linux-s390x',     name: 'linux-s390x',     query: { operating_system: 'linux',   architecture: 's390x' } },
                { id: 'linux-x86',       name: 'linux-x86',       query: { operating_system: 'linux',   architecture: 'x86' } },
                { id: 'linux-x64',       name: 'linux-x64',       query: { operating_system: 'linux',   architecture: 'x64,amd64', libc_type: 'libc,glibc' } },
                { id: 'macos-arm64',     name: 'macos-arm64',     query: { operating_system: 'macos',   architecture: 'arm64,aarch64' } },
                { id: 'macos-x64',       name: 'macos-x64',       query: { operating_system: 'macos',   architecture: 'x64,amd64' } },
                { id: 'solaris-x86',     name: 'solaris-x86',     query: { operating_system: 'solaris', architecture: 'x86' } },
                { id: 'solaris-x64',     name: 'solaris-x64',     query: { operating_system: 'solaris', architecture: 'x64,amd64' } },
                { id: 'solaris-arm64',   name: 'solaris-arm64',   query: { operating_system: 'solaris', architecture: 'arm64,aarch64' } },
                { id: 'solaris-sparc',   name: 'solaris-sparc',   query: { operating_system: 'solaris', architecture: 'sparc' } },
                { id: 'solaris-sparcv9', name: 'solaris-sparcv9', query: { operating_system: 'solaris', architecture: 'sparcv9' } },
                { id: 'windows-arm64',   name: 'windows-arm64',   query: { operating_system: 'windows', architecture: 'arm64,aarch64' } },
                { id: 'windows-ia64',    name: 'windows-ia64',    query: { operating_system: 'windows', architecture: 'ia64' } },
                { id: 'windows-x86',     name: 'windows-x86',     query: { operating_system: 'windows', architecture: 'x86' } },
                { id: 'windows-x64',     name: 'windows-x64',     query: { operating_system: 'windows', architecture: 'x64,amd64' } }
            ],
            product: null,
            version: null,
            type: null,
            platform: null,
            message: "Loading...",
            packages: []
        };
    },
    computed: {
        discoquery: function () {
            const params = new URLSearchParams()
            for (const f of [this.product, this.version, this.type, this.platform]) {
                if (f) {
                    Object.entries(f.query).forEach(e => params.append(...e));
                }
            }
            params.append('latest', 'available');
            return params.toString();
        }
    },
    watch: {
        discoquery: function(query) {
            this.writeHash();
            this.loadlist('?' + query);
        },
        urlhash: function(hash) {
            console.out(hash);
        }
    },
    mounted() {
        this.loadDistributions();
        window.onhashchange = () => {
            this.readHash();
        };
    },
    methods: {
        readHash(hash) {
            const params  = new URLSearchParams(window.location.hash.substring(1));
            this.product  = this.getById(this.productset,  params.get('product'));
            this.version  = this.getById(this.versionset,  params.get('version'));
            this.type     = this.getById(this.typeset,     params.get('type'));
            this.platform = this.getById(this.platformset, params.get('platform'));
        },
        writeHash() {
            const params = new URLSearchParams()
            for (const [id, option] of [['product', this.product], ['version', this.version], ['type', this.type], ['platform', this.platform]]) {
                if (option) {
                    params.append(id, option.id);
                }
            }
            window.location.hash = '#' + params;
        },
        getById(filterlist, id) {
            for (const f of filterlist) {
                if (f.id == id) return f;
            }
            return filterlist[0];
        },
        loadDistributions(query) {
            this.productset = [ { id: 'all', name: 'All', query: {} } ];
            this.discoRequest("distributions", response => {
                for (const d of response.result) {
                    this.productset.push({ id: d.api_parameter, name: d.name, query: { distro: d.api_parameter } })
                }
                this.readHash();
            });
        },
        loadlist(query) {
            this.packages = [];
            this.message = "Loading...";
            const path = "packages" + query;
            this.discoRequest(path, response => {
                this.packages = response.result;
                if (this.packages.length) {
                    this.message = "";
                } else {
                    this.message = "Sorry, no matching packages."
                }
            });
        },
        discoRequest(path, handler) {
            fetch("https://api.foojay.io/disco/v3.0/" + path)
                .then(response => response.json())
                .then(handler);
        }
    }
});
