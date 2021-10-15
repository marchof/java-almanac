Vue.component('sandbox', {
    template: `
        <tabs v-bind:infotext="versioninfo" v-bind:infotooltip="versioninfoext">
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
        axios.get(this.serviceurl("version")).then(response => { 
            this.versioninfo = response.data['java.runtime.version'];
            this.versioninfoext = response.data['java.vendor'] + '\n' + response.data['java.vm.name'];
        })
    },
    methods: {
        serviceurl(action) {
            return "https://sandbox.javaalmanac.io/jdk/" + this.version.replace("java", "") + "/" + action;
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
            showGutter: false,
            showPrintMargin: false,
            maxLines: Infinity,
            useSoftTabs: false,
            showInvisibles: this.$parent.$parent.showInvisibles
        });
        this.editor.setValue(this.source, 1);
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