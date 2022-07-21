Vue.component('sandbox', {
    template: `
        <tabs>
            <template #info>
              <span class="infotext">
                <span v-bind:title="versioninfoext">{{ versioninfo }}</span>
                <select v-model="version">
                  <option>java8</option>
                  <option>java9</option>
                  <option>java10</option>
                  <option>java11</option>
                  <option>java12</option>
                  <option>java13</option>
                  <option>java14</option>
                  <option>java15</option>
                  <option>java16</option>
                  <option>java17</option>
                  <option>java18</option>
                  <option>java19</option>
                  <option>java20</option>
                </select>
              </span>
            </template>
            <slot></slot>
            <tab v-bind:onTabClicked="compileandrun" name="▶︎ Run">
                <div class="sandbox-console">{{ output }}</div>
            </tab>
        </tabs>
    `,
    props: {
        version: { type: String, required: true },
        mainclass: { type: String, required: true },
        preview: { type: Boolean, required: false, default: false },
        showInvisibles: { type: Boolean, required: false, default: false }
    },
    data() {
        return {
            versioninfo: '',
            versioninfoext: '',
            output: ''
        };
    },
    mounted() {
        this.updateversioninfo();
    },
    watch: {
        version: function (oldvalue, newvalue) {
            this.updateversioninfo();
        }
    },
    methods: {
        serviceurl(action) {
            return "https://sandbox.javaalmanac.io/jdk/" + this.version.replace("java", "") + "/" + action;
        },
        updateversioninfo() {
            axios.get(this.serviceurl("version")).then(response => { 
                this.versioninfo = response.data['java.runtime.version'];
                this.versioninfoext = response.data['java.vendor'] + '\n' + response.data['java.vm.name'];
            });
        },
        compileandrun() {
            this.output = "Compile and run with " + this.versioninfo + " ...";
            sourcefiles= [];
            this.$children[0].$children.forEach(tab => {
                if (tab.source) {
                    sourcefiles.push({name: tab.name, content: tab.source});
                }
            });
            request = {
                mainclass: this.mainclass,
                preview: this.preview,
                sourcefiles: sourcefiles
            };
            axios.post(this.serviceurl("compileandrun"), request).then(response => { 
                this.output = response.data.output;
            })
        }
    }
});

Vue.component('sandbox-source', {
    template: `
        <div class="tabs-frame" v-show="active" style="height: 120px;">
        </div>
    `,
    props: {
        name: { required: true }
    },
    data() {
        return {
            active: false,
            editor: Object,
            source: this.getSourceFromSlot()
        };
    },
    mounted() {
        this.editor = window.ace.edit(this.$el, {
            mode: "ace/mode/java",
            theme: 'ace/theme/almanac',
            highlightActiveLine: false,
            showPrintMargin: false,
            showFoldWidgets: false,
            maxLines: Infinity,
            useSoftTabs: false,
            showInvisibles: this.$parent.$parent.showInvisibles
        });
        this.editor.setValue(this.source, -1);
        this.editor.on('change', () => {
            this.source = this.editor.getValue()
        });
    },
    methods: {
        getSourceFromSlot() {
            if (this.$slots.default && this.$slots.default.length) {
                return this.$slots.default[0].text;
            }
            return ''
        },
        onTabClicked() {
            this.editor.focus();
        }
    },
});