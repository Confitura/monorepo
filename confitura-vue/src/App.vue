<template>
  <div id="app">
    <TheMenu/>
    <router-view></router-view>
  </div>
</template>

<style lang="scss">
@import "~normalize.css";

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
<script>
  import { Component, Vue } from 'vue-property-decorator';
  import TheMenu from '@/components/TheMenu.vue';
  @Component({
    components: {TheMenu},
  })
  export default class App extends Vue {
  }
</script>