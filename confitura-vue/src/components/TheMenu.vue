<template>
    <div class="menu" :class="theme">
        <img class="menu__logo" src="../assets/logo_white.svg" alt="">
        <span class="menu__separator"></span>
        <div class="menu__item"
             v-for="item in items" :key="item">
            <a class="menu__link" href="">{{item}}</a>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class TheMenu extends Vue {
        public items = ["Hello", "About", "Speakers", "Partners"];
        public theme = "menu--transparent";

        mounted() {
            window.addEventListener("scroll", this.handleScroll);
        }

        beforeDestroy() {
            window.removeEventListener("scroll", this.handleScroll);
        }

        handleScroll() {
            let scroll = window.scrollY;
            if (scroll > 20) {
                this.setTheme("black");
            } else {
                this.setTheme("transparent");
            }
            // console.log("scroll", window.scrollY);
        }

        private setTheme(theme: string) {
            this.theme = `menu--${theme}`;
        }
    }
</script>

<style scoped lang="scss">
    @import "../assets/colors";

    .menu {
        position: fixed;
        display: flex;
        align-items: center;
        font-weight: bold;
        /*padding: 10px;*/
        padding: 37px 80px 37px 75px;
        /*height: 50px;*/
        width: 100%;
        z-index: 1000;
        box-sizing: border-box;

        &--black {
            background-color: #000000;
            transition: background-color .5s linear;
        }

        &__logo {
            width: 210px;
            height: 100%;
            justify-self: flex-start;
        }

        &__separator {
            flex-grow: 1;
        }

        &__item {
            margin: 0.5rem;
        }

        &__link {
            text-decoration: none;
            color: $brand;
            font-size: 1.5rem;

            &:hover {
                text-decoration: underline;
            }
        }
    }
</style>
