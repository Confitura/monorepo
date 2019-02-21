<template>
  <div id="app">
    <TheHeader/>
    <TheSocialLinks class="standard"/>
    <router-view></router-view>
    <TheContact id="contact"/>
  </div>
</template>

<style lang="scss">
@import '~normalize.css';

@font-face {
  font-family: 'NeuzeitGro-Bol';
  src: url('assets/fonts/30E5AC_0_0.eot');
  src: url('assets/fonts/30E5AC_0_0.woff2') format('woff2'),
    url('assets/fonts/30E5AC_0_0.woff') format('woff'),
    url('assets/fonts/30E5AC_0_0.ttf') format('truetype');
}

@font-face {
  font-family: 'NeuzeitGro-Reg';
  src: url('assets/fonts/30E5AC_1_0.eot');
  src: url('assets/fonts/30E5AC_1_0.woff2') format('woff2'),
    url('assets/fonts/30E5AC_1_0.woff') format('woff'),
    url('assets/fonts/30E5AC_1_0.ttf') format('truetype');
}

#app {
  font-family: 'NeuzeitGro-Reg', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  
}
</style>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TheHeader from '@/components/TheHeader.vue';
import TheSocialLinks from '@/components/TheSocialLinks.vue';
import { WINDOW_RESIZED } from './types';

@Component({
  components: { TheHeader, TheSocialLinks },
})
export default class App extends Vue {
  public mounted() {
    this.resizedCallback();
    window.addEventListener('resize', this.resizedCallback);
  }

  public śbeforeDestroy() {
    window.removeEventListener('resize', this.resizedCallback);
  }

  private resizedCallback = () => {
    const width = window.innerWidth;
    this.$store.commit(WINDOW_RESIZED, { width });
  }
}
</script>
