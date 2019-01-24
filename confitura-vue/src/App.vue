<template>
    <div id="app">
        <TheHeader/>
        <router-view></router-view>
    </div>
</template>

<style lang="scss">
    @import '~normalize.css';

    @font-face {
        font-family: 'NeuzeitGro-Bol';
        src: url('assets/fonts/30E5AC_0_0.eot');
        src: url('assets/fonts/30E5AC_0_0.woff2') format('woff2'), url('assets/fonts/30E5AC_0_0.woff') format('woff'), url('assets/fonts/30E5AC_0_0.ttf') format('truetype');
    }

    @font-face {
        font-family: 'NeuzeitGro-Reg';
        src: url('assets/fonts/30E5AC_1_0.eot');
        src: url('assets/fonts/30E5AC_1_0.woff2') format('woff2'), url('assets/fonts/30E5AC_1_0.woff') format('woff'), url('assets/fonts/30E5AC_1_0.ttf') format('truetype');
    }

    /*html {*/
    /*margin-right: calc(100vw - 100% );*/
    /*}*/

    #app {
        font-family: 'NeuzeitGro-Reg', Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }
</style>
<script>
  import { Component, Vue } from 'vue-property-decorator';
  import TheHeader from '@/components/TheHeader.vue';

  @Component({
    components: { TheHeader },
  })
  export default class App extends Vue {
  }
</script>