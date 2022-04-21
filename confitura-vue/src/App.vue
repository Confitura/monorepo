<template>
  <div id="app">
    Hello!
    <TheHeader />
    <SocialLinks class="standard" />
<!--    <router-view></router-view>-->
  </div>
</template>

<style lang="scss">
@import "styles";
</style>
<script lang="ts">
import { TOKEN, WINDOW_RESIZED } from "./types";
import { defineComponent } from "vue";
import { useStore } from "vuex";
import TheHeader from "@/components/TheHeader.vue";

export default defineComponent({
  name: "App",
  components: { TheHeader },
  setup() {
    const store = useStore();
    return { store };
  },
  mounted() {
    this.resizedCallback();
    window.addEventListener("resize", this.resizedCallback);
    this.store.commit(TOKEN, { token: localStorage.getItem(TOKEN) });
  },
  methods: {
    beforeDestroy() {
      window.removeEventListener("resize", this.resizedCallback);
    },

    resizedCallback() {
      const width = window.innerWidth;
      this.store.commit(WINDOW_RESIZED, { width });
    },
  },
});
</script>
