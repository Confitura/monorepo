<template>
    <div id="app">
        <TheHeader/>
        <TheSocialLinks class="standard"/>
        <router-view></router-view>
    </div>
</template>

<style lang="scss">
    @import 'styles';
</style>
<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import TheHeader from '@/components/TheHeader.vue';
  import TheSocialLinks from '@/components/TheSocialLinks.vue';
  import { TOKEN, WINDOW_RESIZED } from './types';

  @Component({
    components: { TheHeader, TheSocialLinks },
  })
  export default class App extends Vue {
    public mounted() {
      this.resizedCallback();
      window.addEventListener('resize', this.resizedCallback);
      this.$store.commit(TOKEN, { token: localStorage.getItem(TOKEN) });
    }

    public beforeDestroy() {
      window.removeEventListener('resize', this.resizedCallback);
    }

    private resizedCallback = () => {
      const width = window.innerWidth;
      this.$store.commit(WINDOW_RESIZED, { width });
    }
  }
</script>
