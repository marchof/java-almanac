"use strict";

Vue.component('sandbox', {
    template: `
        <tabs v-bind:infotext="versioninfo" v-bind:infotooltip="versioninfoext">
            <slot></slot>
            <tab v-bind:onTabClicked="run" name="▶︎ Run">
                <div class="sandbox-console">{{ "{{" }} output }}</div>
            </tab>
        </tabs>
    `,
    props: {
        version: { type: String, required: true },
        mainclass: { type: String, required: false },
        mainsource: { type: String, required: false },
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
        fetch(this.serviceurl("version"))
            .then(r => r.json())
            .then(response => { 
                this.versioninfo = response['java.runtime.version'];
                this.versioninfoext = response['java.vendor'] + '\n' + response['java.vm.name'];
            });
    },
    methods: {
        serviceurl(action) {
            return `{{ $.Site.Params.Api.Sandbox }}jdk/${this.version.replace("java", "")}/${action}`;
        },
        run() {
            this.output = "Compile and run with " + this.versioninfo + " ...";
            const sourcefiles = [];
            this.$children[0].$children.forEach(tab => {
                if (tab.source) {
                    sourcefiles.push({name: tab.name, content: tab.source});
                }
            });
            request = {
                mainclass: this.mainclass || this.mainsource,
                preview: this.preview,
                sourcefiles: sourcefiles
            };
            fetch(this.serviceurl(this.mainclass ? "compileandrun" : "runfromsource"), { method: "POST", body: JSON.stringify(request) })
                .then(r => r.json())
                .then(response => this.output = response.output );
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