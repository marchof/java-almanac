Vue.component('tabs', {
    template: `
        <div>
            <div class="tabs">
              <span class="infotext" v-bind:title="infotooltip">{{ infotext }}</span>
              <ul>
                <li v-for="tab in tabs" :class="{ 'active': tab.active }">
                    <a href="" @click.prevent="selectTab(tab)">{{ tab.name }}</a>
                </li>
              </ul>
            </div>
            <div class="tabs-content">
                <slot></slot>
            </div>
        </div>
    `,
    props: {
        infotext: '',
        infotooltip: ''
    },
    data() {
        return {
            tabs: []
        };
    },
    created() {
        this.tabs = this.$children;
    },
    mounted() {
        if (this.tabs.length > 0) {
            this.tabs[0].active = true;
        }
    },
    methods: {
        selectTab(selectedTab) {
            this.tabs.forEach(tab => {
                tab.active = (tab.name == selectedTab.name);
                if (tab.active && tab.onTabClicked) {
                    tab.onTabClicked();
                }
            });
        }
    }
});

Vue.component('tab', {
    template: `
        <div class="tabs-frame" v-show="active"><slot></slot></div>
    `,
    props: {
        name: { required: true },
        onTabClicked: null
    },
    data() {
        return {
            active: false
        };
    },
});