Vue.component('downloadlist', {
    template: `
        <div>
        <table>
          <thead>
            <tr>
              <th>Distribution</th>
              <th>Version</th>
              <th>Type</th>
              <th>Platform</th>
              <th>File</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="package in packages">
              <td>{{ package.distribution }}</td>
              <td>{{ package.java_version }}</td>
              <td>{{ package.package_type }}</td>
              <td>{{ package.operating_system }}-{{ package.architecture }}</td>
              <td><a href="" @click.prevent="download(package.id)">{{ package.filename }}</a></td>
            </tr>
          </tbody>
        </table>
        </div>
    `,
    data() {
        return {
            packages: []
        };
    },
    mounted() {
        this.discoRequest("packages/" + window.location.search, response => { 
            this.packages = response.data;
        });
    },
    methods: {
        download(packageid) {
            this.discoRequest("packages/" + packageid, response => {
                this.discoRequest("ephemeral_ids/" + response.data.ephemeral_id, response => {
                    link = response.data.direct_download_uri || response.data.download_site_uri;
                    window.open(link, "_self");
                });
            });
        },
        discoRequest(path, handler) {
            axios.get("https://api.foojay.io/disco/v1.0/" + path).then(handler);
        }
    }
});
