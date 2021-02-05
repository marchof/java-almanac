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
              <td><a href="" @click.prevent="download(package.ephemeral_id)">{{ package.filename }}</a></td>
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
        url = "https://api.foojay.io/disco/v1.0/packages" + window.location.search;
        axios.get(url).then(response => { 
            this.packages = response.data;
        });
    },
    methods: {
        download(ephermalid) {
            url = "https://api.foojay.io/disco/v1.0/ephemeral_ids/" + ephermalid;
            axios.get(url).then(response => {
                link = response.data.direct_download_uri || response.data.download_site_uri;
                window.open(link, "_self");
            });
        }
    }
});
