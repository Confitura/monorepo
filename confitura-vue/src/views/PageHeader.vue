<template>
    <header class="page-header">
        <div class="page-title">frequently asked questions</div>
    </header>
</template>
<script lang="ts">
    import { Component, Vue } from 'vue-property-decorator';
    import { CHANGE_HEADER_THEME } from '@/types';
    @Component({
        components: { },
    })
    export default class PageHeader extends Vue {
        private threshold: number[] = [];

        constructor() {
            super();
            for (let i = 0; i <= 1; i += 0.01) {
                this.threshold.push(i);
            }
        }
        public mounted(): void {
            const options = {
                threshold: this.threshold,
            };

            const callback: IntersectionObserverCallback = (entries) => {
                const [entry] = entries;
                if (entry.isIntersecting) {
                    if (entry.boundingClientRect.top < -100) {
                        this.$store.commit(CHANGE_HEADER_THEME, { color: 'white' });
                    } else {
                        this.$store.commit(CHANGE_HEADER_THEME, { color: 'default' });
                    }
                }
            };
            const observer = new IntersectionObserver(callback, options);
            observer.observe(this.$el);
        }

    }
</script>
<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/fonts";

    .page-header {
        background-color: #000000;
        box-sizing: border-box;
        background-image: url('../assets/stars.png');
        height: 480px;
        width: 100%;
        display: flex;
        padding: $standard-padding;
        align-items: center;
        .page-title {
            color: $brand;
            font-size: 2rem;
            font-family: $font-bold;
        }
    }
</style>