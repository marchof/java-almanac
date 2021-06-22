Vue.component('downloadlist', {
    template: `
        <div>
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
            <tr v-if="loading">
              <td colspan="5">Loading...</td>
            </tr>
            <tr v-if="!loading && !packages.length">
              <td colspan="5">Sorry, no matching packages.</td>
            </tr>
            <tr v-for="package in packages">
              <td>{{ package.distribution }}</td>
              <td>{{ package.java_version }}</td>
              <td>{{ package.package_type }}</td>
              <td>{{ package.operating_system }}-{{ package.architecture }}</td>
              <td style="max-width:400px;overflow:hidden;text-overflow:ellipsis;"><a style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" v-bind:href="package.filename" v-on:click.prevent="download(package.id)">{{ package.filename }}</a></td>
            </tr>
          </tbody>
        </table>
        </div>
    `,
    data() {
        return {
            productset: [
                { id: 'all', name: 'All', query: '' },
                { id: 'aoj', name: 'AdoptOpenJDK', query: 'distro=aoj' },
                { id: 'corretto', name: 'Corretto', query: 'distro=corretto' },
                { id: 'dragonwell', name: 'Dragonwell', query: 'distro=dragonwell' },
                { id: 'liberica', name: 'Liberica', query: 'distro=liberica' },
                { id: 'microsoft', name: 'Microsoft', query: 'distro=microsoft' },
                { id: 'oracle', name: 'Oracle', query: 'distro=oracle' },
                { id: 'oracle_open_jdk', name: 'Oracle OpenJDK', query: 'distro=oracle_open_jdk' },
                { id: 'redhat', name: 'Redhat', query: 'distro=redhat' },
                { id: 'sap_machine', name: 'SAP Machine', query: 'distro=sap_machine' },
                { id: 'zulu', name: 'Zulu', query: 'distro=zulu' }
            ],
            versionset: [
                { id: 'all', name:'All', query: '' },
                { id: '6',  name: '6', query: 'version=6' },
                { id: '7',  name: '7', query: 'version=7' },
                { id: '8',  name: '8', query: 'version=8' },
                { id: '9',  name: '9', query: 'version=9' },
                { id: '10', name: '10', query: 'version=10' },
                { id: '11', name: '11', query: 'version=11' },
                { id: '12', name: '12', query: 'version=12' },
                { id: '13', name: '13', query: 'version=13' },
                { id: '14', name: '14', query: 'version=14' },
                { id: '15', name: '15', query: 'version=15' },
                { id: '16', name: '16', query: 'version=16' },
                { id: '17', name: '17', query: 'version=17-ea' },
                { id: '18', name: '18', query: 'version=18-ea' }
            ],
            typeset: [
                { id: 'all', name: 'All', query: '' },
                { id: 'jdk', name: 'JDK', query: 'package_type=jdk' },
                { id: 'jre', name: 'JRE', query: 'package_type=jre' }
            ],
            platformset: [
                { id: 'all', name: 'All', query: '' },
                { id: 'aix-ppc64', name: 'aix-ppc64', query: 'operating_system=aix&architecture=ppc64' },
                { id: 'alpine-arm64', name: 'alpine-arm64', query: 'libc_type=musl&architecture=arm64,aarch64' },
                { id: 'alpine-x64', name: 'alpine-x64', query: 'libc_type=musl&architecture=x64,amd64' },
                { id: 'linux-arm32', name: 'linux-arm32', query: 'operating_system=linux&architecture=arm' },
                { id: 'linux-arm64', name: 'linux-arm64', query: 'operating_system=linux&architecture=arm64,aarch64' },
                { id: 'linux-ia64', name: 'linux-ia64', query: 'operating_system=linux&architecture=ia64' },
                { id: 'linux-ppc64', name: 'linux-ppc64', query: 'operating_system=linux&architecture=ppc64' },
                { id: 'linux-ppc64le', name: 'linux-ppc64le', query: 'operating_system=linux&architecture=ppc64le' },
                { id: 'linux-s390x', name: 'linux-s390x', query: 'operating_system=linux&architecture=s390x' },
                { id: 'linux-x86', name: 'linux-x86', query: 'operating_system=linux&architecture=x86' },
                { id: 'linux-x64', name: 'linux-x64', query: 'operating_system=linux&architecture=x64,amd64' },
                { id: 'macos-arm64', name: 'macos-arm64', query: 'operating_system=macos&architecture=arm64,aarch64' },
                { id: 'macos-x64', name: 'macos-x64', query: 'operating_system=macos&architecture=x64,amd64' },
                { id: 'solaris-x86', name: 'solaris-x86', query: 'operating_system=solaris&architecture=x86' },
                { id: 'solaris-x64', name: 'solaris-x64', query: 'operating_system=solaris&architecture=x64,amd64' },
                { id: 'solaris-arm64', name: 'solaris-arm64', query: 'operating_system=solaris&architecture=arm64,aarch64' },
                { id: 'solaris-sparc', name: 'solaris-sparc', query: 'operating_system=solaris&architecture=sparc' },
                { id: 'solaris-sparcv9', name: 'solaris-sparcv9', query: 'operating_system=solaris&architecture=sparcv9' },
                { id: 'windows-arm64', name: 'windows-arm64', query: 'operating_system=windows&architecture=arm64,aarch64' },
                { id: 'windows-ia64', name: 'windows-ia64', query: 'operating_system=windows&architecture=ia64' },
                { id: 'windows-x86', name: 'windows-x86', query: 'operating_system=windows&architecture=x86' },
                { id: 'windows-x64', name: 'windows-x64', query: 'operating_system=windows&architecture=x64,amd64' }
            ],
            product: null,
            version: null,
            type: null,
            platform: null,
            loading: true,
            packages: []
        };
    },
    computed: {
        discoquery: function () {
            var q = '';
            for (var f of [this.product, this.version, this.type, this.platform]) {
                if (f && f.query) {
                    if (q) q += '&';
                    q += f.query;
                }
            }
            if (q) q += '&';
            q += 'latest=available';
            return q;
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
        this.readHash();
        window.onhashchange = () => {
            this.readHash();
        };
    },
    methods: {
        readHash(hash) {
            var hash = window.location.hash.substring(1);
            var params = {};
            for (var keyvalue of hash.split('&')) {
                keyvalue = keyvalue.split('=');
                params[decodeURIComponent(keyvalue[0])] = decodeURIComponent(keyvalue[1]);
            }
            this.product  = this.getById(this.productset, params['product']);
            this.version  = this.getById(this.versionset, params['version']);
            this.type     = this.getById(this.typeset, params['type']);
            this.platform = this.getById(this.platformset, params['platform']);
        },
        writeHash() {
            h = '';
            for (var f of [['product', this.product], ['version', this.version], ['type', this.type], ['platform', this.platform]]) {
                if (f[1] && f[1].query) {
                    if (h) h += '&';
                    h += f[0] + '=' + f[1].id;
                }
            }
            console.log('#' + h)
            window.location.hash = '#' + h;
        },
        getById(filterlist, id) {
            for (var f of filterlist) {
                if (f.id == id) return f;
            }
            return filterlist[0];
        },
        loadlist(query) {
            this.packages = [];
            this.loading = true;
            var path = "packages" + query;
            this.discoRequest(path, response => {
                this.packages = response.data.result;
                this.loading = false;
            });
        },
        download(packageid) {
            this.discoRequest("packages/" + packageid, response => {
                this.discoRequest("ephemeral_ids/" + response.data.result[0].ephemeral_id, response => {
                    var item = response.data.result[0];
                    link = item.direct_download_uri || item.download_site_uri;
                    window.open(link, "_self");
                });
            });
        },
        discoRequest(path, handler) {
            axios.get("https://api.foojay.io/disco/v2.0/" + path).then(handler);
        }
    }
});
